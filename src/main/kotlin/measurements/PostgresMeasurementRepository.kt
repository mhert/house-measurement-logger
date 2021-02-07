package measurements

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.Timestamp
import java.util.*

class PostgresMeasurementRepository(private val db: Connection) : MeasurementRepository {
    private val insertMeasurementStatement: PreparedStatement = db.prepareStatement(
        "INSERT INTO measurements(measurement_id, sensor_id, measurement_date, measurement_value) VALUES (?, ?, ?, ?)"
    )

    override fun addMeasurement(measurement: Measurement) {
        insertMeasurementStatement.setString(1, UUID.randomUUID().toString())
        insertMeasurementStatement.setString(2, measurement.sensorId.toString())
        insertMeasurementStatement.setTimestamp(3, Timestamp.from(measurement.measurementDate))
        insertMeasurementStatement.setDouble(4, measurement.value)
        insertMeasurementStatement.executeUpdate()
    }
}
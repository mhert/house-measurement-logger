package measurements

import com.impossibl.postgres.jdbc.PGSQLSimpleException
import java.sql.Connection
import java.sql.Timestamp
import java.util.*

class PostgresMeasurementRepository(private val db: Connection) : MeasurementRepository {

    init{
        db.nativeSQL("DISCARD ALL");
    }

    private val insertMeasurementStatement = db.prepareStatement(
        "INSERT INTO measurements(measurement_id, sensor_id, measurement_date, measurement_value) VALUES (?, ?, ?, ?)"
    )

    override fun addMeasurement(measurement: Measurement) {

        insertMeasurementStatement.setString(1, UUID.randomUUID().toString())
        insertMeasurementStatement.setString(2, measurement.sensorId.toString())
        insertMeasurementStatement.setTimestamp(3, Timestamp.from(measurement.measurementDate))
        insertMeasurementStatement.setDouble(4, measurement.value)
        try {
            insertMeasurementStatement.executeUpdate()
        } catch (e: PGSQLSimpleException) {
            val parts = e.message?.split('"') ?: throw e
            if (parts.size != 3) {
                throw e
            }

            db.nativeSQL("DEALLOCATE " + parts[1])
        }
    }
}

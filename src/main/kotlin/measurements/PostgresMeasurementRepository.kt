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

    private val findMeasurementStatementById = db.prepareStatement(
        "SELECT * FROM measurements WHERE measurement_id = ?"
    )

    @Synchronized override fun addMeasurement(measurement: Measurement) {

        var measurementId: UUID

        do {
             measurementId = UUID.randomUUID()
        } while (existsMeasurementWithId(measurementId))

        insertMeasurementStatement.setString(1, measurementId.toString())
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

    fun existsMeasurementWithId(measurementId: UUID): Boolean {

        findMeasurementStatementById.setString(1, measurementId.toString())

        try {
            findMeasurementStatementById.execute()
        } catch (e: PGSQLSimpleException) {
            val parts = e.message?.split('"') ?: throw e
            if (parts.size != 3) {
                throw e
            }

            db.nativeSQL("DEALLOCATE " + parts[1])
        }

        return findMeasurementStatementById.resultSet.fetchSize == 0
    }
}

package housemeasurementlogger.measurements

import java.time.Instant
import java.util.*

data class NewIncomingMeasurement(
    val sensorId: UUID,
    val sensorName: String,
    val measurementDate: Instant,
    val value: Double,
)

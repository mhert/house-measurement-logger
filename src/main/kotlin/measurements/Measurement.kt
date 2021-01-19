package measurements

import java.time.Instant
import java.util.*

data class Measurement(
    val sensorName: String,
    val measurementDate: Instant,
    val value: Double
)

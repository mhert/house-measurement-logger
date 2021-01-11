package measurements

import java.time.Instant

data class Measurement(
    val sensorName: String,
    val measurementDate: Instant,
    val value: Double
)

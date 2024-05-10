package housemeasurementlogger.knx_sensors

import housemeasurementlogger.knx.DPT
import housemeasurementlogger.knx.GroupAddress
import java.util.*

class KnxSensor(
    val id: UUID,
    val name: String,
    val address: GroupAddress,
    val type: DPT,
)

package knx_sensors

import knx.DPT
import knx.GroupAddress
import java.util.*

class KnxSensor(
    val id: UUID,
    val name: String,
    val address: GroupAddress,
    val type: DPT,
)

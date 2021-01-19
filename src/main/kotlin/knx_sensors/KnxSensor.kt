package knx_sensors

import knx.DPT
import knx.GroupAddress

class KnxSensor(
    val name: String,
    val address: GroupAddress,
    val type: DPT,
)

package housemeasurementlogger.knx_sensors

import housemeasurementlogger.knx.DPT
import housemeasurementlogger.knx.GroupAddress
import housemeasurementlogger.knx.GroupAddresses
import java.io.File
import java.util.*

class KnxSensorsFile(fileName: String) : KnxSensors {

    private val sensorsByGroupAddress: Map<GroupAddress, housemeasurementlogger.knx_sensors.KnxSensor> = File(fileName).readLines().map {
        val (id, name, address, typeId) = it.split(";")
        housemeasurementlogger.knx_sensors.KnxSensor(
            UUID.fromString(id),
            name,
            GroupAddress.fromString(address),
            DPT.fromTypeId(typeId)
        )
    }.associateBy { it.address }

    override fun groupAddresses(): GroupAddresses {
        return GroupAddresses(sensorsByGroupAddress.map { it.value.address })
    }

    override fun sensorForGroupAddress(address: GroupAddress): housemeasurementlogger.knx_sensors.KnxSensor {
        return sensorsByGroupAddress[address]!!
    }

    override val size: Int
        get() = sensorsByGroupAddress.size

    override fun contains(element: housemeasurementlogger.knx_sensors.KnxSensor): Boolean {
        return sensorsByGroupAddress.contains(element.address)
    }

    override fun containsAll(elements: Collection<housemeasurementlogger.knx_sensors.KnxSensor>): Boolean {
        return elements.fold(true) { acc, element -> acc && this.contains(element) }
    }

    override fun isEmpty(): Boolean {
        return sensorsByGroupAddress.isEmpty()
    }

    override fun iterator(): Iterator<housemeasurementlogger.knx_sensors.KnxSensor> {
        return sensorsByGroupAddress.values.iterator()
    }

}

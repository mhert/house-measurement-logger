package knx_sensors

import knx.DPT
import knx.GroupAddress
import knx.GroupAddresses
import java.io.File

class KnxSensorsFile(fileName: String) : KnxSensors {

    private val sensorsByGroupAddress: Map<GroupAddress, KnxSensor> = File(fileName).readLines().map {
        val (name, address, typeId) = it.split(";")
        KnxSensor(name, GroupAddress.fromString(address), DPT.fromTypeId(typeId))
    }.associateBy { it.address }

    override fun groupAddresses(): GroupAddresses {
        return GroupAddresses(sensorsByGroupAddress.map { it.value.address })
    }

    override fun sensorForGroupAddress(address: GroupAddress): KnxSensor {
        return sensorsByGroupAddress[address]!!
    }

    override val size: Int
        get() = sensorsByGroupAddress.size

    override fun contains(element: KnxSensor): Boolean {
        return sensorsByGroupAddress.contains(element.address)
    }

    override fun containsAll(elements: Collection<KnxSensor>): Boolean {
        return elements.fold(true) { acc, element -> acc && this.contains(element) }
    }

    override fun isEmpty(): Boolean {
        return sensorsByGroupAddress.isEmpty()
    }

    override fun iterator(): Iterator<KnxSensor> {
        return sensorsByGroupAddress.values.iterator()
    }

}

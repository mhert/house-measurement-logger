package modbus

import java.io.File

class ModBusDeviceSensorsFile(fileName: String) : ModBusDeviceSensors {

    private val sensorsByName: Map<String, ModBusDeviceSensor> = File(fileName).readLines().map {
        val (name, unit, register, length, type) = it.split(";")
        ModBusDeviceSensor(name, unit.toInt(), register.toInt(), length.toInt(), ModBusDeviceSensor.Type.fromType(type),)
    }.associateBy { it.name }

    override val size: Int
        get() = sensorsByName.size

    override fun contains(element: ModBusDeviceSensor): Boolean {
        return sensorsByName.contains(element.name)
    }

    override fun containsAll(elements: Collection<ModBusDeviceSensor>): Boolean {
        return elements.fold(true) { acc, element -> acc && this.contains(element) }
    }

    override fun isEmpty(): Boolean {
        return sensorsByName.isEmpty()
    }

    override fun iterator(): Iterator<ModBusDeviceSensor> {
        return sensorsByName.values.iterator()
    }
}
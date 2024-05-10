package housemeasurementlogger.modbus

import java.io.File
import java.util.*

class ModBusDeviceSensorsFile(fileName: String) : ModBusDeviceSensors {

    private val sensorsByName: Map<String, ModBusDeviceSensor> = File(fileName).readLines().map {
        val sensorValues = it.split(";")
        val id = sensorValues[0]
        val name = sensorValues[1]
        val unit = sensorValues[2]
        val register = sensorValues[3]
        val length = sensorValues[4]
        val type = sensorValues[5]
        ModBusDeviceSensor(UUID.fromString(id), name, unit.toInt(), register.toInt(), length.toInt(), ModBusDeviceSensor.Type.fromType(type),)
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
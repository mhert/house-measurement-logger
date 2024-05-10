package housemeasurementlogger.inverter

import java.io.File
import java.util.*

class InverterSensorsFile(fileName: String) : InverterSensors {

    private val sensorsByName: Map<String, InverterSensor> = File(fileName).readLines().map {
        val (id, name, datasetFieldName, type) = it.split(";")
        InverterSensor(UUID.fromString(id), name, datasetFieldName, InverterSensor.Type.fromType(type))
    }.associateBy { it.name }

    override val size: Int
        get() = sensorsByName.size

    override fun contains(element: InverterSensor): Boolean {
        return sensorsByName.contains(element.datasetFieldName)
    }

    override fun containsAll(elements: Collection<InverterSensor>): Boolean {
        return elements.fold(true) { acc, element -> acc && this.contains(element) }
    }

    override fun isEmpty(): Boolean {
        return sensorsByName.isEmpty()
    }

    override fun iterator(): Iterator<InverterSensor> {
        return sensorsByName.values.iterator()
    }
}
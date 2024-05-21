package housemeasurementlogger.modbus

import java.io.File
import java.util.*

class FileBasedModBusSensorsRepository(private val sensorsFileName: String) :
    ModBusSensorsRepository {
    override fun allSensors(): Collection<ModBusDeviceSensor> {
        return File(sensorsFileName).readLines().map {
            val sensorValues = it.split(";")
            val id = sensorValues[0]
            val name = sensorValues[1]
            val unit = sensorValues[2]
            val register = sensorValues[3]
            val length = sensorValues[4]
            val type = sensorValues[5]
            ModBusDeviceSensor(
                UUID.fromString(id),
                name,
                unit.toInt(),
                register.toInt(),
                length.toInt(),
                ModBusDeviceSensor.Type.fromType(type),
            )
        }
    }
}

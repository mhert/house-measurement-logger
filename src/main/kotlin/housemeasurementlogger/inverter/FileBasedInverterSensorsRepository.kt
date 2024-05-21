package housemeasurementlogger.inverter

import java.io.File
import java.util.*

class FileBasedInverterSensorsRepository(private val sensorsFileName: String) :
    InverterSensorsRepository {
    override fun allSensors(): Collection<InverterSensor> {
        return File(sensorsFileName).readLines().map {
            val (id, name, datasetFieldName, type) = it.split(";")
            InverterSensor(
                UUID.fromString(id),
                name,
                datasetFieldName,
                InverterSensor.Type.fromType(type)
            )
        }
    }
}

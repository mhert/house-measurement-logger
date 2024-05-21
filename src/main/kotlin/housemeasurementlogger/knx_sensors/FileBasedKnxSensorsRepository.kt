package housemeasurementlogger.knx_sensors

import housemeasurementlogger.knx.DPT
import housemeasurementlogger.knx.GroupAddress
import java.io.File
import java.util.*

class FileBasedKnxSensorsRepository(private val sensorsFileName: String) : KnxSensorsRepository {
    override fun allSensors(): Collection<KnxSensor> {
        return File(sensorsFileName).readLines().map {
            val (id, name, address, typeId) = it.split(";")
            KnxSensor(
                UUID.fromString(id),
                name,
                GroupAddress.fromString(address),
                DPT.fromTypeId(typeId)
            )
        }
    }
}

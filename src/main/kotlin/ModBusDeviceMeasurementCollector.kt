import modbus.ModBusDevice
import measurements.Measurement
import measurements.MeasurementRepository
import modbus.ModBusDeviceSensor
import modbus.ModBusDeviceSensors
import java.time.Clock

class ModBusDeviceMeasurementCollector constructor(
    private val sensors: ModBusDeviceSensors,
    private val modBusDevice: ModBusDevice,
    private val measurementRepository: MeasurementRepository,
    private val clock: Clock
) {
    fun collect() {
        for (sensor in sensors) {
            when(sensor.type) {
                ModBusDeviceSensor.Type.TYPE_DOUBLE -> {
                    val data = modBusDevice.getDoubleDataForSensor(sensor)
                    measurementRepository.addMeasurement(
                        Measurement(
                            sensor.name,
                            clock.instant(),
                            data
                        )
                    )
                }
            }
        }
    }
}

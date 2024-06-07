package housemeasurementlogger

import housemeasurementlogger.measurements.NewIncomingMeasurement
import housemeasurementlogger.modbus.ModBusDevice
import housemeasurementlogger.modbus.ModBusDeviceSensor
import housemeasurementlogger.modbus.ModBusSensorsRepository
import java.time.Clock
import org.springframework.context.ApplicationEventPublisher

class ModBusDeviceMeasurementCollector(
    private val modBusSensorsRepository: ModBusSensorsRepository,
    private val modBusDevice: ModBusDevice,
    private val eventPublisher: ApplicationEventPublisher,
    private val clock: Clock
) {
    fun collect() {
        val sensors = modBusSensorsRepository.allSensors()

        for (sensor in sensors) {
            when (sensor.type) {
                ModBusDeviceSensor.Type.TYPE_DOUBLE -> {
                    val data = modBusDevice.getDoubleDataForSensor(sensor)

                    eventPublisher.publishEvent(
                        NewIncomingMeasurement(sensor.id, sensor.name, clock.instant(), data)
                    )
                }
            }
        }
    }
}

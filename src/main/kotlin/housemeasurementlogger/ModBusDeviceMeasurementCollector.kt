package housemeasurementlogger

import housemeasurementlogger.measurements.Measurement
import housemeasurementlogger.measurements.MeasurementRepository
import housemeasurementlogger.modbus.ModBusDevice
import housemeasurementlogger.modbus.ModBusDeviceSensor
import housemeasurementlogger.modbus.ModBusSensorsRepository
import java.time.Clock

class ModBusDeviceMeasurementCollector(
    private val modBusSensorsRepository: ModBusSensorsRepository,
    private val modBusDevice: ModBusDevice,
    private val measurementRepository: MeasurementRepository,
    private val clock: Clock
) {
    fun collect() {
        val sensors = modBusSensorsRepository.allSensors()

        for (sensor in sensors) {
            when (sensor.type) {
                ModBusDeviceSensor.Type.TYPE_DOUBLE -> {
                    val data = modBusDevice.getDoubleDataForSensor(sensor)
                    measurementRepository.addMeasurement(
                        Measurement(sensor.id, sensor.name, clock.instant(), data)
                    )
                }
            }
        }
    }
}

package housemeasurementlogger

import housemeasurementlogger.inverter.Inverter
import housemeasurementlogger.inverter.InverterSensor
import housemeasurementlogger.inverter.InverterSensorsRepository
import housemeasurementlogger.measurements.NewIncomingMeasurement
import java.time.Clock
import org.springframework.context.ApplicationEventPublisher

class InverterMeasurementCollector(
    private val inverterSensorsRepository: InverterSensorsRepository,
    private val inverter: Inverter,
    private val eventPublisher: ApplicationEventPublisher,
    private val clock: Clock,
) {
    suspend fun collect() {
        val data = inverter.instantData()
        val sensors = inverterSensorsRepository.allSensors()

        data.onSuccess {
            for (sensor in sensors) {
                when (sensor.type) {
                    InverterSensor.Type.TYPE_DOUBLE -> {
                        eventPublisher.publishEvent(
                            NewIncomingMeasurement(
                                sensor.id,
                                sensor.name,
                                clock.instant(),
                                it.getDouble(sensor.datasetFieldName),
                            )
                        )
                    }
                }
            }
        }
    }
}

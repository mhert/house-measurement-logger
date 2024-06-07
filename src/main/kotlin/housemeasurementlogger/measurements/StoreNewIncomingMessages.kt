package housemeasurementlogger.measurements

import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class StoreNewIncomingMessages(
    private val measurementRepository: MeasurementRepository,
) {
    @EventListener
    fun invoke(newIncomingMeasurement: NewIncomingMeasurement) {
        measurementRepository.addMeasurement(
            Measurement(
                newIncomingMeasurement.sensorId,
                newIncomingMeasurement.sensorName,
                newIncomingMeasurement.measurementDate,
                newIncomingMeasurement.value,
            )
        )
    }
}

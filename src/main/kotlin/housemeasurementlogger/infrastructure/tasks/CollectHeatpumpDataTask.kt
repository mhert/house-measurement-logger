package housemeasurementlogger.infrastructure.tasks

import housemeasurementlogger.ModBusDeviceMeasurementCollector
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class CollectHeatpumpDataTask(
    private var heatPumpMeasurementCollector: ModBusDeviceMeasurementCollector,
) {
    @Scheduled(initialDelay = 1000, fixedRate = 30000)
    fun collect() {
        heatPumpMeasurementCollector.collect()
    }
}

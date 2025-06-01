package housemeasurementlogger.infrastructure.tasks

import housemeasurementlogger.InverterMeasurementCollector
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class CollectInverterDataTask(
    private var inverterMeasurementCollector: InverterMeasurementCollector
) {
    @Scheduled(initialDelay = 1000, fixedRate = 30000)
    suspend fun collect() {
        inverterMeasurementCollector.collect()
    }
}

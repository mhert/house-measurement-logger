package housemeasurementlogger.infrastructure.tasks

import housemeasurementlogger.KnxMeasurementCollector
import io.calimero.link.KNXNetworkLink
import io.calimero.process.ProcessCommunicatorImpl
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * This Task will never end because the thread, started by Calimero is not under our control. Maybe
 * there is a more elegant way, to start a background task, but for now it's fine
 */
@Component
class CollectKnxDataTask(
    private val knxMeasurementCollector: KnxMeasurementCollector,
    private val knxLink: KNXNetworkLink,
) {
    @Scheduled(initialDelay = 1000)
    fun collect() {
        val knxPollTimeMs: Long = 1000

        // This uses a Thread, started in ConnectionBase::startReceiver
        ProcessCommunicatorImpl(knxLink).use { processCommunicator ->
            processCommunicator.addProcessListener(knxMeasurementCollector)

            while (knxLink.isOpen) {
                Thread.sleep(knxPollTimeMs)
            }

            if (!knxLink.isOpen) {
                throw RuntimeException("Lost connection to knx")
            }
        }
    }
}

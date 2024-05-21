package housemeasurementlogger.infrastructure

import housemeasurementlogger.InverterMeasurementCollector
import housemeasurementlogger.KnxMeasurementCollector
import housemeasurementlogger.ModBusDeviceMeasurementCollector
import housemeasurementlogger.infrastructure.configuration.DryRunConfiguration
import housemeasurementlogger.infrastructure.configuration.PostgreSqlDatabaseBackendConfiguration
import io.calimero.link.KNXNetworkLinkIP
import io.calimero.process.ProcessCommunicatorImpl
import kotlin.system.exitProcess
import kotlinx.coroutines.*
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@ConfigurationPropertiesScan
@Import(DryRunConfiguration::class, PostgreSqlDatabaseBackendConfiguration::class)
open class HouseManagementLoggerApplication(
    private var inverterMeasurementCollector: InverterMeasurementCollector,
    private var heatPumpMeasurementCollector: ModBusDeviceMeasurementCollector,
    private val knxMeasurementCollector: KnxMeasurementCollector,
    private val knxLink: KNXNetworkLinkIP,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        Thread.setDefaultUncaughtExceptionHandler { _, ex ->
            println(ex)
            exitProcess(1)
        }

        val inverterPollTimeMs: Long = 30000
        val heatPumpPollTimeMs: Long = 30000
        val knxPollTimeMs: Long = 1000

        CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                inverterMeasurementCollector.collect()
                delay(inverterPollTimeMs)
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                heatPumpMeasurementCollector.collect()
                delay(heatPumpPollTimeMs)
            }
        }

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

@ExperimentalUnsignedTypes
fun main(args: Array<String>) {
    runApplication<HouseManagementLoggerApplication>(*args)
}

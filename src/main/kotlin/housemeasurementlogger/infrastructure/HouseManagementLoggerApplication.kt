package housemeasurementlogger.infrastructure

import com.ghgande.j2mod.modbus.facade.ModbusTCPMaster
import housemeasurementlogger.InverterMeasurementCollector
import housemeasurementlogger.KnxMeasurementCollector
import housemeasurementlogger.ModBusDeviceMeasurementCollector
import housemeasurementlogger.infrastructure.configuration.DryRunConfiguration
import housemeasurementlogger.infrastructure.configuration.HouseMeasurementLoggerConfigProperties
import housemeasurementlogger.infrastructure.configuration.PostgreSqlDatabaseBackendConfiguration
import housemeasurementlogger.knx_sensors.KnxSensorsFile
import housemeasurementlogger.measurements.MeasurementRepository
import housemeasurementlogger.modbus.FileBasedModBusSensorsRepository
import java.net.InetSocketAddress
import java.time.Clock
import kotlin.system.exitProcess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import
import tuwien.auto.calimero.link.KNXNetworkLinkIP
import tuwien.auto.calimero.link.medium.TPSettings
import tuwien.auto.calimero.process.ProcessCommunicatorImpl

@SpringBootApplication
@ConfigurationPropertiesScan
@Import(DryRunConfiguration::class, PostgreSqlDatabaseBackendConfiguration::class)
open class HouseManagementLoggerApplication(
    private var config: HouseMeasurementLoggerConfigProperties,
    private var measurementRepository: MeasurementRepository,
    private var inverterMeasurementCollector: InverterMeasurementCollector,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        Thread.setDefaultUncaughtExceptionHandler { _, ex ->
            println(ex)
            exitProcess(1)
        }

        val knxGatewayAddress = config.knxGatewayAddress
        val knxGatewayPort = config.knxGatewayPort
        val knxSensorsDescriptionFile = config.knxSensorsDescriptionFile
        val heatPumpSensorsDescriptionFile = config.heatPumpSensorsDescriptionFile
        val heatPumpHost = config.heatPumpHost

        val inverterPollTimeMs: Long = 30000
        val heatPumpPollTimeMs: Long = 30000
        val knxPollTimeMs: Long = 1000

        val localAddress = InetSocketAddress(0)
        val gatewayAddress = InetSocketAddress(knxGatewayAddress, knxGatewayPort.toInt())

        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                inverterMeasurementCollector.collect()
                delay(inverterPollTimeMs)
            }
        }

        val heatPumpSensorsRepository = FileBasedModBusSensorsRepository(heatPumpSensorsDescriptionFile)

        ModbusTCPMaster(heatPumpHost).let { heatPumpModbus ->
            housemeasurementlogger.modbus.J2ModModBusDevice(heatPumpModbus).let { heatPump ->
                ModBusDeviceMeasurementCollector(
                        heatPumpSensorsRepository,
                        heatPump,
                        measurementRepository,
                        Clock.systemDefaultZone()
                    )
                    .let { heatPumpMeasurementCollector ->
                        object : Thread() {
                                override fun run() {
                                    while (heatPumpModbus.isConnected) {
                                        heatPumpMeasurementCollector.collect()
                                        sleep(heatPumpPollTimeMs)
                                    }

                                    if (!heatPumpModbus.isConnected) {
                                        throw RuntimeException(
                                            "Lost connection to modbus device $heatPumpHost"
                                        )
                                    }
                                }
                            }
                            .start()
                    }
            }
        }

        // This runs in a thread as well. It is started in AbstractLink:186
        KNXNetworkLinkIP.newTunnelingLink(localAddress, gatewayAddress, true, TPSettings.TP1).use {
            knxLink ->
            ProcessCommunicatorImpl(knxLink).use { processCommunicator ->
                processCommunicator.addProcessListener(
                    KnxMeasurementCollector(
                        KnxSensorsFile(knxSensorsDescriptionFile),
                        measurementRepository,
                        Clock.systemDefaultZone()
                    )
                )
                while (knxLink.isOpen) {
                    Thread.sleep(knxPollTimeMs)
                }

                if (!knxLink.isOpen) {
                    throw RuntimeException("Lost connection to knx $knxGatewayAddress")
                }
            }
        }
    }
}

@ExperimentalUnsignedTypes
fun main(args: Array<String>) {
    runApplication<HouseManagementLoggerApplication>(*args)
}

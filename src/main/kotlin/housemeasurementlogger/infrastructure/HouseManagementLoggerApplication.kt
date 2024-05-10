package housemeasurementlogger.infrastructure

import com.ghgande.j2mod.modbus.facade.ModbusTCPMaster
import com.impossibl.postgres.jdbc.PGDataSource
import housemeasurementlogger.InverterMeasurementCollector
import housemeasurementlogger.KnxMeasurementCollector
import housemeasurementlogger.ModBusDeviceMeasurementCollector
import housemeasurementlogger.modbus.ModBusDeviceSensorsFile
import housemeasurementlogger.inverter.HttpBasedInverter
import housemeasurementlogger.inverter.InverterSensorsFile
import housemeasurementlogger.knx_sensors.KnxSensorsFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import housemeasurementlogger.measurements.PostgresMeasurementRepository
import housemeasurementlogger.measurements.PrintingMeasurementRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import tuwien.auto.calimero.link.KNXNetworkLinkIP
import tuwien.auto.calimero.link.medium.TPSettings
import tuwien.auto.calimero.process.ProcessCommunicatorImpl
import java.lang.RuntimeException
import java.net.InetSocketAddress
import java.time.Clock
import kotlin.system.exitProcess

@SpringBootApplication
@ConfigurationPropertiesScan
open class HouseManagementLoggerApplication : CommandLineRunner {
    @Autowired
    private lateinit var config: HouseMeasurementLoggerConfigProperties

    override fun run(vararg args: String?) {
        Thread.setDefaultUncaughtExceptionHandler { _, ex ->
            println(ex)
            exitProcess(1)
        }

        val knxGatewayAddress = config.knxGatewayAddress
        val knxGatewayPort = config.knxGatewayPort
        val dbType = config.dbType
        val postgresqlDbServerName = config.postgresqlHostName
        val postgresqlDbPort = config.postgresqlDbPort
        val postgresqlDbDatabaseName = config.postgresqlDbDatabaseName
        val postgresqlDbUserName = config.postgresqlDbUserName
        val postgresqlDbPassword = config.postgresqlDbPassword
        val knxSensorsDescriptionFile = config.knxSensorsDescriptionFile
        val inverterSensorsDescriptionFile = config.inverterSensorsDescriptionFile
        val heatPumpSensorsDescriptionFile = config.heatPumpSensorsDescriptionFile
        val inverterBaseUrl = config.inverterBaseUrl
        val heatPumpHost = config.heatPumpHost

        val inverterPollTimeMs: Long = 30000
        val heatPumpPollTimeMs: Long = 30000
        val knxPollTimeMs: Long = 1000

        val localAddress = InetSocketAddress(0)
        val gatewayAddress = InetSocketAddress(knxGatewayAddress, knxGatewayPort.toInt())

        val measurementRepository = when (dbType) {
            "postgres" -> {
                val dataSource = PGDataSource()
                dataSource.serverName = postgresqlDbServerName
                dataSource.port = postgresqlDbPort.toInt()
                dataSource.databaseName = postgresqlDbDatabaseName
                dataSource.user = postgresqlDbUserName
                dataSource.password = postgresqlDbPassword
                PostgresMeasurementRepository(
                    dataSource.connection
                )
            }
            "dry-run" -> PrintingMeasurementRepository()
            else -> throw RuntimeException("Invalid backend-type")
        }

        HttpBasedInverter(inverterBaseUrl).let { inverter ->
            InverterMeasurementCollector(
                InverterSensorsFile(inverterSensorsDescriptionFile),
                inverter,
                measurementRepository,
                Clock.systemDefaultZone()
            ).let { inverterMeasurementCollector ->
                CoroutineScope(Dispatchers.IO).launch {
                    while (true) {
                        inverterMeasurementCollector.collect()
                        delay(inverterPollTimeMs)
                    }
                }
            }
        }


        ModbusTCPMaster(heatPumpHost).let { heatPumpModbus ->
            housemeasurementlogger.modbus.J2ModModBusDevice(heatPumpModbus).let { heatPump ->
                ModBusDeviceMeasurementCollector(
                    ModBusDeviceSensorsFile(heatPumpSensorsDescriptionFile),
                    heatPump,
                    measurementRepository,
                    Clock.systemDefaultZone()
                ).let { heatPumpMeasurementCollector ->
                    object : Thread() {
                        override fun run() {
                            while (heatPumpModbus.isConnected) {
                                heatPumpMeasurementCollector.collect()
                                sleep(heatPumpPollTimeMs)
                            }

                            if (!heatPumpModbus.isConnected) {
                                throw RuntimeException("Lost connection to modbus device $heatPumpHost")
                            }
                        }
                    }.start()
                }
            }
        }

        // This runs in a thread as well. It is started in AbstractLink:186
        KNXNetworkLinkIP.newTunnelingLink(
            localAddress,
            gatewayAddress,
            true,
            TPSettings.TP1
        ).use { knxLink ->
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

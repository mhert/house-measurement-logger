import com.ghgande.j2mod.modbus.facade.ModbusTCPMaster
import com.impossibl.postgres.jdbc.PGDataSource
import modbus.ModBusDeviceSensorsFile
import modbus.J2ModModBusDevice
import infrastructure.ArgOrEnvParser
import inverter.HttpBasedInverter
import inverter.InverterSensorsFile
import knx_sensors.KnxSensorsFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import measurements.PostgresMeasurementRepository
import measurements.PrintingMeasurementRepository
import tuwien.auto.calimero.link.KNXNetworkLinkIP
import tuwien.auto.calimero.link.medium.TPSettings
import tuwien.auto.calimero.process.ProcessCommunicatorImpl
import java.lang.RuntimeException
import java.net.InetSocketAddress
import java.time.Clock
import kotlin.system.exitProcess

@ExperimentalUnsignedTypes
fun main(args: Array<String>) {
    val argEnvParser = ArgOrEnvParser("house-data-logger", args, System.getenv())

    val knxGatewayAddress = argEnvParser.requiredString("knxGatewayAddress", "KNX_GATEWAY_ADDRESS")
    val knxGatewayPort = argEnvParser.requiredInt("knxGatewayPort", "KNX_GATEWAY_PORT")
    val dbType = argEnvParser.requiredString("dbType", "DB_TYPE")
    val postgresqlDbServerName = argEnvParser.optionalString("pgDbHostName", "PG_DB_HOST_NAME", "")
    val postgresqlDbPort = argEnvParser.optionalInt("pgDbPort", "PG_DB_PORT", 5432)
    val postgresqlDbDatabaseName = argEnvParser.optionalString("pgDbDatabaseName", "PG_DB_DATABASE_NAME", "")
    val postgresqlDbUserName = argEnvParser.optionalString("pgDbUserName", "PG_DB_USER_NAME", "")
    val postgresqlDbPassword = argEnvParser.optionalString("pgDbPassword", "PG_DB_PASSWORD", "")
    val knxSensorsDescriptionFile = argEnvParser.requiredString("knxSensorsDescriptionFile", "KNX_SENSORS_DESCRIPTION_FILE")
    val inverterSensorsDescriptionFile = argEnvParser.requiredString("inverterSensorsDescriptionFile", "INVERTER_SENSORS_DESCRIPTION_FILE")
    val heatPumpSensorsDescriptionFile = argEnvParser.requiredString("heatPumpSensorsDescriptionFile", "HEAT_PUMP_SENSORS_DESCRIPTION_FILE")
    val inverterBaseUrl = argEnvParser.requiredString("inverterBaseUrl", "INVERTER_BASE_URL")
    val heatPumpHost = argEnvParser.requiredString("heatPumpHost", "HEAT_PUMP_HOST")

    val inverterPollTimeMs: Long = 30000
    val heatPumpPollTimeMs: Long = 30000
    val knxPollTimeMs: Long = 1000

    argEnvParser.parse()

    val localAddress = InetSocketAddress(0)
    val gatewayAddress = InetSocketAddress(knxGatewayAddress.toString(), knxGatewayPort.toInt())

    Thread.setDefaultUncaughtExceptionHandler { _, ex ->
        println(ex)
        exitProcess(1)
    }

    val measurementRepository = when (dbType.toString()) {
        "postgres" -> {
            val dataSource = PGDataSource()
            dataSource.serverName = postgresqlDbServerName.toString()
            dataSource.port = postgresqlDbPort.toInt()
            dataSource.databaseName = postgresqlDbDatabaseName.toString()
            dataSource.user = postgresqlDbUserName.toString()
            dataSource.password = postgresqlDbPassword.toString()
            PostgresMeasurementRepository(
                dataSource.connection
            )
        }
        "dry-run" -> PrintingMeasurementRepository()
        else -> throw RuntimeException("Invalid backend-type")
    }

    HttpBasedInverter(inverterBaseUrl.toString()).let { inverter ->
        InverterMeasurementCollector(
            InverterSensorsFile(inverterSensorsDescriptionFile.toString()),
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


    ModbusTCPMaster(heatPumpHost.toString()).let { heatPumpModbus ->
        J2ModModBusDevice(heatPumpModbus).let { heatPump ->
            ModBusDeviceMeasurementCollector(
                ModBusDeviceSensorsFile(heatPumpSensorsDescriptionFile.toString()),
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
                    KnxSensorsFile(knxSensorsDescriptionFile.toString()),
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

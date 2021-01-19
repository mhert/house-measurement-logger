import com.ghgande.j2mod.modbus.facade.ModbusTCPMaster
import heatpump.ModBusBasedHeatPump
import infrastructure.ArgOrEnvParser
import inverter.HttpBasedInverter
import inverter.InverterSensorsFile
import knx_sensors.KnxSensorsFile
import measurements.MongoMeasurementRepository
import measurements.PrintingMeasurementRepository
import org.litote.kmongo.KMongo
import tuwien.auto.calimero.link.KNXNetworkLinkIP
import tuwien.auto.calimero.link.medium.TPSettings
import tuwien.auto.calimero.process.ProcessCommunicatorImpl
import java.net.InetSocketAddress
import java.time.Clock
import kotlin.system.exitProcess

@ExperimentalUnsignedTypes
fun main(args: Array<String>) {
    val argEnvParser = ArgOrEnvParser("house-data-logger", args, System.getenv())

    val knxGatewayAddress = argEnvParser.requiredString("knxGatewayAddress", "KNX_GATEWAY_ADDRESS")
    val knxGatewayPort = argEnvParser.requiredInt("knxGatewayPort", "KNX_GATEWAY_PORT")
    val dbConnectionString = argEnvParser.requiredString("dbConnectionString", "DB_CONNECTION_STRING")
    val dbName = argEnvParser.requiredString("dbName", "DB_NAME")
    val knxSensorsDescriptionFile = argEnvParser.requiredString("knxSensorsDescriptionFile", "KNX_SENSORS_DESCRIPTION_FILE")
    val inverterSensorsDescriptionFile = argEnvParser.requiredString("inverterSensorsDescriptionFile", "INVERTER_SENSORS_DESCRIPTION_FILE")
    val inverterBaseUrl = argEnvParser.requiredString("inverterBaseUrl", "INVERTER_BASE_URL")
    val heatPumpHost = argEnvParser.requiredString("heatPumpHost", "HEAT_PUMP_HOST")

    val inverterPollTimeMs: Long = 30000;
    val heatPumpPollTimeMs: Long = 30000;

    val dryRun = false

    argEnvParser.parse()

    try {
        val localAddress = InetSocketAddress(0)
        val gatewayAddress = InetSocketAddress(knxGatewayAddress.toString(), knxGatewayPort.toInt())

        val measurementRepository = if (dryRun) {
            PrintingMeasurementRepository()
        } else {
            MongoMeasurementRepository(
                KMongo.createClient(dbConnectionString.toString()).getDatabase(dbName.toString())
            )
        }

        HttpBasedInverter(inverterBaseUrl.toString()).let { inverter ->
            InverterMeasurementCollector(
                InverterSensorsFile(inverterSensorsDescriptionFile.toString()),
                inverter,
                measurementRepository,
                Clock.systemDefaultZone()
            ).let { inverterMeasurementCollector ->
                object : Thread() {
                    public override fun run() {
                        while (true) {
                            inverterMeasurementCollector.collect()
                            Thread.sleep(inverterPollTimeMs)
                        }
                    }
                }.start()
            }
        }


        ModbusTCPMaster(heatPumpHost.toString()).let { heatPumpModbus ->
            ModBusBasedHeatPump(heatPumpModbus).let { heatPump ->
                HeatPumpMeasurementCollector(
                    heatPump,
                    measurementRepository,
                    Clock.systemDefaultZone()
                ).let { heatPumpMeasurementCollector ->
                    object : Thread() {
                        override fun run() {
                            while (heatPumpModbus.isConnected) {
                                heatPumpMeasurementCollector.collect()
                                Thread.sleep(heatPumpPollTimeMs)
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
                while (knxLink.isOpen) Thread.sleep(500)
            }
        }
    } catch (e: Exception) {
        System.err.println(e)
        exitProcess(1)
    }
}

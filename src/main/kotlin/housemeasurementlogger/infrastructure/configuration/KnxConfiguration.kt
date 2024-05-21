package housemeasurementlogger.infrastructure.configuration

import housemeasurementlogger.KnxMeasurementCollector
import housemeasurementlogger.inverter.Inverter
import housemeasurementlogger.knx_sensors.FileBasedKnxSensorsRepository
import housemeasurementlogger.knx_sensors.KnxSensorsRepository
import housemeasurementlogger.measurements.MeasurementRepository
import io.calimero.link.KNXNetworkLinkIP
import io.calimero.link.medium.TPSettings
import java.net.InetSocketAddress
import java.time.Clock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class KnxConfiguration {
    @Bean
    open fun knxLink(
        config: HouseMeasurementLoggerConfigProperties,
    ): KNXNetworkLinkIP {
        val localAddress = InetSocketAddress(0)
        val gatewayAddress =
            InetSocketAddress(config.knxGatewayAddress, config.knxGatewayPort.toInt())

        return KNXNetworkLinkIP.newTunnelingLink(localAddress, gatewayAddress, true, TPSettings())
    }

    @Bean
    open fun knxMeasurementCollector(
        knxSensorsRepository: KnxSensorsRepository,
        inverter: Inverter,
        measurementRepository: MeasurementRepository,
        clock: Clock,
    ): KnxMeasurementCollector {
        return KnxMeasurementCollector(knxSensorsRepository, measurementRepository, clock)
    }

    @Bean
    open fun knxSensorsRepository(
        config: HouseMeasurementLoggerConfigProperties,
    ): KnxSensorsRepository {
        return FileBasedKnxSensorsRepository(config.knxSensorsDescriptionFile)
    }
}

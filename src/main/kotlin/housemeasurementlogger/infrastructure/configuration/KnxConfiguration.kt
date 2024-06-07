package housemeasurementlogger.infrastructure.configuration

import housemeasurementlogger.knx_sensors.FileBasedKnxSensorsRepository
import housemeasurementlogger.knx_sensors.KnxSensorsRepository
import io.calimero.link.KNXNetworkLinkIP
import io.calimero.link.medium.TPSettings
import java.net.InetSocketAddress
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KnxConfiguration {
    @Bean
    fun knxLink(
        config: HouseMeasurementLoggerConfigProperties,
    ): KNXNetworkLinkIP {
        val localAddress = InetSocketAddress(0)
        val gatewayAddress =
            InetSocketAddress(config.knxGatewayAddress, config.knxGatewayPort.toInt())

        return KNXNetworkLinkIP.newTunnelingLink(localAddress, gatewayAddress, true, TPSettings())
    }

    @Bean
    fun knxSensorsRepository(
        config: HouseMeasurementLoggerConfigProperties,
    ): KnxSensorsRepository {
        return FileBasedKnxSensorsRepository(config.knxSensorsDescriptionFile)
    }
}

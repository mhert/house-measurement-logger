package housemeasurementlogger.infrastructure.configuration

import housemeasurementlogger.inverter.FileBasedInverterSensorsRepository
import housemeasurementlogger.inverter.HttpBasedInverter
import housemeasurementlogger.inverter.Inverter
import housemeasurementlogger.inverter.InverterSensorsRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class InverterConfiguration {

    @Bean
    fun inverterSensorsRepository(
        config: HouseMeasurementLoggerConfigProperties,
    ): InverterSensorsRepository {
        return FileBasedInverterSensorsRepository(config.inverterSensorsDescriptionFile)
    }

    @Bean
    fun inverter(
        config: HouseMeasurementLoggerConfigProperties,
    ): Inverter {
        return HttpBasedInverter(config.inverterBaseUrl)
    }
}

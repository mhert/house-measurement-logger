package housemeasurementlogger.infrastructure.configuration

import housemeasurementlogger.InverterMeasurementCollector
import housemeasurementlogger.inverter.FileBasedInverterSensorsRepository
import housemeasurementlogger.inverter.HttpBasedInverter
import housemeasurementlogger.inverter.Inverter
import housemeasurementlogger.inverter.InverterSensorsRepository
import housemeasurementlogger.measurements.MeasurementRepository
import java.time.Clock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class InverterConfiguration {
    @Bean
    open fun inverterMeasurementCollector(
        inverterSensorsRepository: InverterSensorsRepository,
        inverter: Inverter,
        measurementRepository: MeasurementRepository,
        clock: Clock,
    ): InverterMeasurementCollector {
        return InverterMeasurementCollector(
            inverterSensorsRepository,
            inverter,
            measurementRepository,
            clock
        )
    }

    @Bean
    open fun inverterSensorsRepository(
        config: HouseMeasurementLoggerConfigProperties,
    ): InverterSensorsRepository {
        return FileBasedInverterSensorsRepository(config.inverterSensorsDescriptionFile)
    }

    @Bean
    open fun inverter(
        config: HouseMeasurementLoggerConfigProperties,
    ): Inverter {
        return HttpBasedInverter(config.inverterBaseUrl)
    }
}

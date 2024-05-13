package housemeasurementlogger.infrastructure

import housemeasurementlogger.measurements.MeasurementRepository
import housemeasurementlogger.measurements.PrintingMeasurementRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("dry-run")
open class DryRunConfiguration {
    @Bean
    open fun measurementRepository(): MeasurementRepository {
        return PrintingMeasurementRepository()
    }
}
package housemeasurementlogger.infrastructure.configuration

import housemeasurementlogger.measurements.DryRunMeasurementRepository
import housemeasurementlogger.measurements.MeasurementRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("dry-run")
class DryRunConfiguration {
    @Bean
    fun measurementRepository(): MeasurementRepository {
        return DryRunMeasurementRepository()
    }
}

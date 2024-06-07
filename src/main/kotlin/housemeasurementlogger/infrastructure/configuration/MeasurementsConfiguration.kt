package housemeasurementlogger.infrastructure.configuration

import housemeasurementlogger.measurements.MeasurementRepository
import housemeasurementlogger.measurements.StoreNewIncomingMessages
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MeasurementsConfiguration {
    @Bean
    fun storeNewIncomingMessages(
        measurementRepository: MeasurementRepository
    ): StoreNewIncomingMessages {
        return StoreNewIncomingMessages(measurementRepository)
    }
}

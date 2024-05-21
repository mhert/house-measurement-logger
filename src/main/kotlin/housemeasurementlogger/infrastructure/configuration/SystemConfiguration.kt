package housemeasurementlogger.infrastructure.configuration

import java.time.Clock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SystemConfiguration {
    @Bean
    open fun clock(): Clock {
        return Clock.systemDefaultZone()
    }
}

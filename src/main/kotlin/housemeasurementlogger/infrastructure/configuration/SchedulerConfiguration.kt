package housemeasurementlogger.infrastructure.configuration

import housemeasurementlogger.infrastructure.tasks.TasksErrorHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler

@Configuration
class SchedulerConfiguration(
    private val errorHandler: TasksErrorHandler,
) {
    @Bean
    fun taskScheduler(): TaskScheduler {
        val taskScheduler = SimpleAsyncTaskScheduler()
        // TODO: here we could set the errorHandler, if it would be supported by
        // SimpleAsyncTaskScheduler
        // In spring-context 6.2 there will be a method, but for now we have to wait
        // https://github.com/spring-projects/spring-framework/issues/32460
        return taskScheduler
    }
}

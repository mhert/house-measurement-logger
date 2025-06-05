package housemeasurementlogger.infrastructure.configuration

import housemeasurementlogger.infrastructure.tasks.TasksErrorHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler

@Configuration
class SchedulerConfiguration(private val errorHandler: TasksErrorHandler) {
    @Bean
    fun taskScheduler(): TaskScheduler {
        val taskScheduler = SimpleAsyncTaskScheduler()
        taskScheduler.setErrorHandler(errorHandler)
        return taskScheduler
    }
}

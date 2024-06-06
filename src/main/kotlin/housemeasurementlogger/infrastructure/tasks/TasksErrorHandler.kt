package housemeasurementlogger.infrastructure.tasks

import kotlin.system.exitProcess
import org.springframework.boot.SpringApplication
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import org.springframework.util.ErrorHandler

@Component
class TasksErrorHandler(
    private val context: ApplicationContext,
) : ErrorHandler {
    override fun handleError(t: Throwable) {
        exitProcess(SpringApplication.exit(context))
    }
}

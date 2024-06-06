package housemeasurementlogger.infrastructure.aspects

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component

@Aspect
@Component
class LogStoredMeasurement {
    @Before(
        "execution(void housemeasurementlogger.measurements.MeasurementRepository.addMeasurement(housemeasurementlogger.measurements.Measurement))"
    )
    fun logMeasurement(joinPoint: JoinPoint) {
        System.getLogger(this::class.java.getName())
            .log(System.Logger.Level.INFO, joinPoint.args[0])
    }
}

import heatpump.HeatPump
import measurements.Measurement
import measurements.MeasurementRepository
import java.time.Clock

class HeatPumpMeasurementCollector constructor(
    private val heatPump: HeatPump,
    private val measurementRepository: MeasurementRepository,
    private val clock: Clock
) {
    fun collect() {
        val data = heatPump.instantData()

        measurementRepository.addMeasurement(
            Measurement(
                "HeatPump outsideTemperature",
                clock.instant(),
                data.outsideTemperature,
            )
        )
        measurementRepository.addMeasurement(
            Measurement(
                "HeatPump outgoingTemperature",
                clock.instant(),
                data.outgoingTemperature,
            )
        )
        measurementRepository.addMeasurement(
            Measurement(
                "HeatPump returnTemperature",
                clock.instant(),
                data.returnTemperature,
            )
        )
        measurementRepository.addMeasurement(
            Measurement(
                "HeatPump waterTemperatureBottom",
                clock.instant(),
                data.waterTemperatureBottom,
            )
        )
        measurementRepository.addMeasurement(
            Measurement(
                "HeatPump waterTemperatureTop",
                clock.instant(),
                data.waterTemperatureTop,
            )
        )
    }
}

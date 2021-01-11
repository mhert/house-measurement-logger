import inverter.HttpBasedInverter
import measurements.Measurement
import measurements.MeasurementRepository
import java.time.Clock

class InverterMeasurementCollector(
    private val inverter: HttpBasedInverter,
    private val measurementRepository: MeasurementRepository,
    private val clock: Clock,
) {
    fun collect() {
        val data = inverter.instantData()

        measurementRepository.addMeasurement(
            Measurement(
                "PV producedToday",
                clock.instant(),
                data.producedToday,
            )
        )
        measurementRepository.addMeasurement(
            Measurement(
                "PV producedYear",
                clock.instant(),
                data.producedYear,
            )
        )
        measurementRepository.addMeasurement(
            Measurement(
                "PV producedTotal",
                clock.instant(),
                data.producedTotal,
            )
        )
        measurementRepository.addMeasurement(
            Measurement(
                "PV production instant",
                clock.instant(),
                data.pvProduction,
            )
        )
        measurementRepository.addMeasurement(
            Measurement(
                "Grid consumption instant",
                clock.instant(),
                data.gridConsumption,
            )
        )
        measurementRepository.addMeasurement(
            Measurement(
                "House consumption instant",
                clock.instant(),
                data.totalConsumption,
            )
        )
    }
}
package housemeasurementlogger

import housemeasurementlogger.inverter.HttpBasedInverter
import housemeasurementlogger.inverter.InverterSensor
import housemeasurementlogger.inverter.InverterSensors
import housemeasurementlogger.measurements.Measurement
import housemeasurementlogger.measurements.MeasurementRepository
import java.time.Clock

class InverterMeasurementCollector(
    private val sensors: InverterSensors,
    private val inverter: HttpBasedInverter,
    private val measurementRepository: MeasurementRepository,
    private val clock: Clock,
) {
    suspend fun collect() {
        val data = inverter.instantData()

        for (sensor in sensors) {
            when(sensor.type) {
                InverterSensor.Type.TYPE_DOUBLE -> {
                    measurementRepository.addMeasurement(
                        Measurement(
                            sensor.id,
                            sensor.name,
                            clock.instant(),
                            data.getDouble(sensor.datasetFieldName)
                        )
                    )
                }
            }
        }
    }
}

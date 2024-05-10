package housemeasurementlogger.measurements

interface MeasurementRepository {
    fun addMeasurement(measurement: Measurement)
}

package measurements

interface MeasurementRepository {
    fun addMeasurement(measurement: Measurement)

    fun allMeasurements(): MeasurementCollection
}

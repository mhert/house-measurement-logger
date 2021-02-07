package measurements

class PrintingMeasurementRepository: MeasurementRepository {
    override fun addMeasurement(measurement: Measurement) {
        println(measurement)
    }
}
package measurements

class PrintingMeasurementRepository: MeasurementRepository {
    override fun addMeasurement(measurement: Measurement) {
        println(measurement)
    }

    override fun allMeasurements(): MeasurementCollection {
        TODO("Not yet implemented")
    }
}
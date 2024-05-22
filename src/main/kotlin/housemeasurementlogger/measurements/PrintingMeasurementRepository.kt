package housemeasurementlogger.measurements

class PrintingMeasurementRepository : MeasurementRepository {
    override fun addMeasurement(measurement: Measurement) {
        System.getLogger(this::class.java.getName()).log(System.Logger.Level.INFO, measurement)
    }
}

package housemeasurementlogger.inverter

interface InverterSensorsRepository {
    fun allSensors(): Collection<InverterSensor>
}

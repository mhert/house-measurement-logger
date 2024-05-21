package housemeasurementlogger.knx_sensors

interface KnxSensorsRepository {
    fun allSensors(): Collection<KnxSensor>
}

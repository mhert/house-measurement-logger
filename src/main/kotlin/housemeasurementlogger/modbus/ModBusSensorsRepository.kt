package housemeasurementlogger.modbus

interface ModBusSensorsRepository {
    fun allSensors(): Collection<ModBusDeviceSensor>
}

package housemeasurementlogger.modbus

interface ModBusDevice {
    fun getDoubleDataForSensor(sensor: ModBusDeviceSensor): Double
}

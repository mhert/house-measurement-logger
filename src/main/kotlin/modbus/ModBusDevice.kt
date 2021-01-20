package modbus

interface ModBusDevice {
    fun getDoubleDataForSensor(sensor: ModBusDeviceSensor): Double
}

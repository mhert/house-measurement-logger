package housemeasurementlogger.knx_sensors

import housemeasurementlogger.knx.GroupAddress
import housemeasurementlogger.knx.GroupAddresses

interface KnxSensors : Collection<housemeasurementlogger.knx_sensors.KnxSensor> {
    fun groupAddresses(): GroupAddresses

    fun sensorForGroupAddress(address: GroupAddress): housemeasurementlogger.knx_sensors.KnxSensor
}

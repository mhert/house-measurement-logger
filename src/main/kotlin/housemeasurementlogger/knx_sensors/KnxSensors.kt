package housemeasurementlogger.knx_sensors

import housemeasurementlogger.knx.GroupAddress
import housemeasurementlogger.knx.GroupAddresses

interface KnxSensors : Collection<KnxSensor> {
    fun groupAddresses(): GroupAddresses

    fun sensorForGroupAddress(address: GroupAddress): KnxSensor
}

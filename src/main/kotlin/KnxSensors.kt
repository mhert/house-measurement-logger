import knx.GroupAddress
import knx.GroupAddresses

interface KnxSensors : Collection<KnxSensor> {
    fun groupAddresses(): GroupAddresses

    fun sensorForGroupAddress(address: GroupAddress): KnxSensor
}

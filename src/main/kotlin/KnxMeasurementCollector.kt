import knx.DPT
import knx.GroupAddress
import knx.TwoByteFloatValue
import knx_sensors.KnxSensor
import knx_sensors.KnxSensors
import measurements.Measurement
import measurements.MeasurementRepository
import tuwien.auto.calimero.DetachEvent
import tuwien.auto.calimero.process.ProcessEvent
import tuwien.auto.calimero.process.ProcessListener
import java.time.Clock

class KnxMeasurementCollector(
    private val sensors: KnxSensors,
    private val measurementRepository: MeasurementRepository,
    private val clock: Clock,
) : ProcessListener {

    override fun groupWrite(e: ProcessEvent?) {
        val destination = GroupAddress.fromString(e?.destination.toString())

        if (!isThisListenerResponsible(destination)) {
            return
        }

        val destinationSensor = responsibleSensor(destination)

        val value = when (destinationSensor.type) {
            DPT.TEMPERATURE -> TwoByteFloatValue(DPT.TEMPERATURE, ProcessListener.asFloat(e))
            DPT.INTENSITY_OF_LIGHT -> TwoByteFloatValue(DPT.INTENSITY_OF_LIGHT, ProcessListener.asFloat(e))
            DPT.WIND_SPEED -> TwoByteFloatValue(DPT.WIND_SPEED, ProcessListener.asFloat(e))
        }

        measurementRepository.addMeasurement(
            Measurement(
                destinationSensor.id,
                destinationSensor.name,
                clock.instant(),
                value.toDouble()
            )
        )
    }

    private fun isThisListenerResponsible(destination: GroupAddress): Boolean {
        return sensors.groupAddresses().contains(destination)
    }

    private fun responsibleSensor(destination: GroupAddress): KnxSensor {
        return sensors.sensorForGroupAddress(destination)
    }

    override fun groupReadRequest(e: ProcessEvent?) {}

    override fun groupReadResponse(e: ProcessEvent?) {}

    override fun detached(e: DetachEvent?) {}
}

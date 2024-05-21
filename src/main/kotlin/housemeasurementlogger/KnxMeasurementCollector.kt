package housemeasurementlogger

import housemeasurementlogger.knx.DPT
import housemeasurementlogger.knx.GroupAddress
import housemeasurementlogger.knx.OneByteIntValue
import housemeasurementlogger.knx.TwoByteFloatValue
import housemeasurementlogger.knx_sensors.KnxSensor
import housemeasurementlogger.knx_sensors.KnxSensors
import housemeasurementlogger.measurements.Measurement
import housemeasurementlogger.measurements.MeasurementRepository
import io.calimero.DetachEvent
import io.calimero.process.ProcessEvent
import io.calimero.process.ProcessListener
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

        val value: Double =
            when (destinationSensor.type) {
                DPT.PERCENT ->
                    OneByteIntValue(DPT.PERCENT, ProcessListener.asUnsigned(e, "5.001"))
                        .value
                        .toDouble()
                DPT.TEMPERATURE ->
                    TwoByteFloatValue(DPT.TEMPERATURE, ProcessListener.asFloat(e)).value
                DPT.INTENSITY_OF_LIGHT ->
                    TwoByteFloatValue(DPT.INTENSITY_OF_LIGHT, ProcessListener.asFloat(e)).value
                DPT.WIND_SPEED ->
                    TwoByteFloatValue(DPT.WIND_SPEED, ProcessListener.asFloat(e)).value
            }

        measurementRepository.addMeasurement(
            Measurement(destinationSensor.id, destinationSensor.name, clock.instant(), value)
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

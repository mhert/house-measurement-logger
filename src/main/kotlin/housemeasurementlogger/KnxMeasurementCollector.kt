package housemeasurementlogger

import housemeasurementlogger.knx.DPT
import housemeasurementlogger.knx.GroupAddress
import housemeasurementlogger.knx.OneByteIntValue
import housemeasurementlogger.knx.TwoByteFloatValue
import housemeasurementlogger.knx_sensors.KnxSensor
import housemeasurementlogger.knx_sensors.KnxSensorsRepository
import housemeasurementlogger.measurements.NewIncomingMeasurement
import io.calimero.DetachEvent
import io.calimero.process.ProcessEvent
import io.calimero.process.ProcessListener
import java.time.Clock
import org.springframework.context.ApplicationEventPublisher

class KnxMeasurementCollector(
    private val knxSensorsRepository: KnxSensorsRepository,
    private val eventPublisher: ApplicationEventPublisher,
    private val clock: Clock,
) : ProcessListener {
    override fun groupWrite(e: ProcessEvent?) {
        val destination = GroupAddress.fromString(e?.destination.toString())

        if (!isThisListenerResponsible(destination)) {
            return
        }

        val destinationSensor = responsibleSensor(destination)

        if (destinationSensor === null) {
            return
        }

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

        eventPublisher.publishEvent(
            NewIncomingMeasurement(
                destinationSensor.id,
                destinationSensor.name,
                clock.instant(),
                value,
            )
        )
    }

    private fun isThisListenerResponsible(destination: GroupAddress): Boolean {
        val sensors = knxSensorsRepository.allSensors()
        val groupAddressesForSensors = sensors.map { it.address }

        return groupAddressesForSensors.contains(destination)
    }

    private fun responsibleSensor(destination: GroupAddress): KnxSensor? {
        val sensors = knxSensorsRepository.allSensors()

        return sensors.find { it.address == destination }
    }

    override fun groupReadRequest(e: ProcessEvent?) {}

    override fun groupReadResponse(e: ProcessEvent?) {}

    override fun detached(e: DetachEvent?) {}
}

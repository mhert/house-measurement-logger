package housemeasurementlogger.infrastructure.configuration

import housemeasurementlogger.InverterMeasurementCollector
import housemeasurementlogger.KnxMeasurementCollector
import housemeasurementlogger.ModBusDeviceMeasurementCollector
import housemeasurementlogger.inverter.Inverter
import housemeasurementlogger.inverter.InverterSensorsRepository
import housemeasurementlogger.knx_sensors.KnxSensorsRepository
import housemeasurementlogger.modbus.ModBusDevice
import housemeasurementlogger.modbus.ModBusSensorsRepository
import java.time.Clock
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CollectorsConfiguration {
    @Bean
    fun heatPumpMeasurementCollector(
        heatPumpSensorsRepository: ModBusSensorsRepository,
        heatPump: ModBusDevice,
        eventPublisher: ApplicationEventPublisher,
        clock: Clock,
    ): ModBusDeviceMeasurementCollector {
        return ModBusDeviceMeasurementCollector(
            heatPumpSensorsRepository,
            heatPump,
            eventPublisher,
            clock,
        )
    }

    @Bean
    fun inverterMeasurementCollector(
        inverterSensorsRepository: InverterSensorsRepository,
        inverter: Inverter,
        eventPublisher: ApplicationEventPublisher,
        clock: Clock,
    ): InverterMeasurementCollector {
        return InverterMeasurementCollector(
            inverterSensorsRepository,
            inverter,
            eventPublisher,
            clock,
        )
    }

    @Bean
    fun knxMeasurementCollector(
        knxSensorsRepository: KnxSensorsRepository,
        inverter: Inverter,
        eventPublisher: ApplicationEventPublisher,
        clock: Clock,
    ): KnxMeasurementCollector {
        return KnxMeasurementCollector(knxSensorsRepository, eventPublisher, clock)
    }
}

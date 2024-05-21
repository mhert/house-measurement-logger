package housemeasurementlogger.infrastructure.configuration

import com.ghgande.j2mod.modbus.facade.ModbusTCPMaster
import housemeasurementlogger.ModBusDeviceMeasurementCollector
import housemeasurementlogger.measurements.MeasurementRepository
import housemeasurementlogger.modbus.FileBasedModBusSensorsRepository
import housemeasurementlogger.modbus.J2ModModBusDevice
import housemeasurementlogger.modbus.ModBusDevice
import housemeasurementlogger.modbus.ModBusSensorsRepository
import java.time.Clock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class HeatPumpConfiguration {
    @Bean
    open fun heatPumpMeasurementCollector(
        heatPumpSensorsRepository: ModBusSensorsRepository,
        heatPump: ModBusDevice,
        measurementRepository: MeasurementRepository,
        clock: Clock,
    ): ModBusDeviceMeasurementCollector {
        return ModBusDeviceMeasurementCollector(
            heatPumpSensorsRepository,
            heatPump,
            measurementRepository,
            clock
        )
    }

    @Bean
    open fun heatPumpSensorsRepository(
        config: HouseMeasurementLoggerConfigProperties,
    ): ModBusSensorsRepository {
        return FileBasedModBusSensorsRepository(config.heatPumpSensorsDescriptionFile)
    }

    @Bean
    open fun heatPump(
        config: HouseMeasurementLoggerConfigProperties,
    ): ModBusDevice {
        return J2ModModBusDevice(ModbusTCPMaster(config.heatPumpHost))
    }
}
package housemeasurementlogger.infrastructure.configuration

import com.ghgande.j2mod.modbus.facade.ModbusTCPMaster
import housemeasurementlogger.modbus.CachingModBusSensorsRepository
import housemeasurementlogger.modbus.FileBasedModBusSensorsRepository
import housemeasurementlogger.modbus.J2ModModBusDevice
import housemeasurementlogger.modbus.ModBusDevice
import housemeasurementlogger.modbus.ModBusSensorsRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HeatPumpConfiguration {

    @Bean
    fun heatPumpSensorsRepository(
        config: HouseMeasurementLoggerConfigProperties
    ): ModBusSensorsRepository {
        return CachingModBusSensorsRepository(
            FileBasedModBusSensorsRepository(config.heatPumpSensorsDescriptionFile)
        )
    }

    @Bean
    fun heatPump(config: HouseMeasurementLoggerConfigProperties): ModBusDevice {
        return J2ModModBusDevice(ModbusTCPMaster(config.heatPumpHost))
    }
}

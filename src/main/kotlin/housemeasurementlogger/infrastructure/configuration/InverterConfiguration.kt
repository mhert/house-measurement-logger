package housemeasurementlogger.infrastructure.configuration

import housemeasurementlogger.inverter.CachingInverterSensorsRepository
import housemeasurementlogger.inverter.FileBasedInverterSensorsRepository
import housemeasurementlogger.inverter.HttpBasedInverter
import housemeasurementlogger.inverter.Inverter
import housemeasurementlogger.inverter.InverterSensorsRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.*
import io.ktor.client.features.HttpTimeout
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class InverterConfiguration {

    @Bean
    fun inverterSensorsRepository(
        config: HouseMeasurementLoggerConfigProperties
    ): InverterSensorsRepository {
        return CachingInverterSensorsRepository(
            FileBasedInverterSensorsRepository(config.inverterSensorsDescriptionFile)
        )
    }

    @Bean
    fun inverter(config: HouseMeasurementLoggerConfigProperties): Inverter {
        val client =
            HttpClient(CIO) {
                install(HttpTimeout)
                install(JsonFeature) {
                    serializer =
                        KotlinxSerializer(
                            kotlinx.serialization.json.Json {
                                prettyPrint = true
                                isLenient = true
                                ignoreUnknownKeys = true
                            }
                        )
                }
            }

        return HttpBasedInverter(client, config.inverterBaseUrl)
    }
}

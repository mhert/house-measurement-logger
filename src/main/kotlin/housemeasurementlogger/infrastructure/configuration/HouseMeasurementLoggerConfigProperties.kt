package housemeasurementlogger.infrastructure.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties

@ConfigurationProperties(prefix = "house-measurement-logger")
@EnableConfigurationProperties
data class HouseMeasurementLoggerConfigProperties(
    var knxGatewayAddress: String,
    var knxGatewayPort: String,
    var dbType: String,
    var postgresqlHostName: String,
    var postgresqlDbPort: String,
    var postgresqlDbDatabaseName: String,
    var postgresqlDbUserName: String,
    var postgresqlDbPassword: String,
    var knxSensorsDescriptionFile: String,
    var inverterSensorsDescriptionFile: String,
    var heatPumpSensorsDescriptionFile: String,
    var inverterBaseUrl: String,
    var heatPumpHost: String,
)

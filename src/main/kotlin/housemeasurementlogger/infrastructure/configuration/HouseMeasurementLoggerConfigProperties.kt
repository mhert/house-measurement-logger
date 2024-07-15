package housemeasurementlogger.infrastructure.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "house-measurement-logger")
data class HouseMeasurementLoggerConfigProperties(
    var printMeasurements: String,
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

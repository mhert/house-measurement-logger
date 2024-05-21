package housemeasurementlogger.infrastructure.configuration

import com.impossibl.postgres.jdbc.PGDataSource
import housemeasurementlogger.measurements.MeasurementRepository
import housemeasurementlogger.measurements.PostgresMeasurementRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("postgresql-backend")
open class PostgreSqlDatabaseBackendConfiguration {
    @Bean
    open fun measurementRepositoryDataSource(
        config: HouseMeasurementLoggerConfigProperties
    ): PGDataSource {
        val dataSource = PGDataSource()

        dataSource.serverName = config.postgresqlHostName
        dataSource.port = config.postgresqlDbPort.toInt()
        dataSource.databaseName = config.postgresqlDbDatabaseName
        dataSource.user = config.postgresqlDbUserName
        dataSource.password = config.postgresqlDbPassword

        return dataSource
    }

    @Bean
    open fun measurementRepository(dataSource: PGDataSource): MeasurementRepository {
        return PostgresMeasurementRepository(dataSource.connection)
    }
}

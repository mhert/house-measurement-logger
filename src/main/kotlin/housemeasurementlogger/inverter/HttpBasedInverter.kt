package housemeasurementlogger.inverter

import io.ktor.client.*
import io.ktor.client.features.HttpRequestTimeoutException
import io.ktor.client.features.timeout
import io.ktor.client.request.*

class HttpBasedInverter(private val client: HttpClient, private val inverterBaseUrl: String) :
    Inverter {

    override suspend fun instantData(): Result<InstantData> {
        try {
            val response: HttpBasedInverterResponse =
                client.get<HttpBasedInverterResponse> {
                    url("$inverterBaseUrl/solar_api/v1/GetPowerFlowRealtimeData.fcgi")
                    timeout { requestTimeoutMillis = 3000 }
                }

            val producedToday: Double = response.Body.Data.Inverters["1"]!!.E_Day ?: 0.0
            val producedYear: Double = response.Body.Data.Inverters["1"]!!.E_Year ?: 0.0
            val producedTotal: Double = response.Body.Data.Inverters["1"]!!.E_Total ?: 0.0
            val pvProduction: Double = response.Body.Data.Site.P_PV ?: 0.0
            val gridConsumption: Double = response.Body.Data.Site.P_Grid ?: 0.0
            val totalConsumption: Double = response.Body.Data.Site.P_Load ?: 0.0

            return Result.success(
                InstantData(
                    producedToday,
                    producedYear,
                    producedTotal,
                    pvProduction,
                    gridConsumption,
                    totalConsumption,
                )
            )
        } catch (e: HttpRequestTimeoutException) {
            return Result.failure(e)
        }
    }
}

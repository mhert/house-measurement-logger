package inverter

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*

class HttpBasedInverter(private val inverterBaseUrl: String) : Inverter {
    override suspend fun instantData(): InstantData {
        val client = HttpClient(CIO) {
            install(JsonFeature) {
                serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
        }

        val response: HttpBasedInverterResponse = client.get("$inverterBaseUrl/solar_api/v1/GetPowerFlowRealtimeData.fcgi")

        val producedToday: Double = response.Body.Data.Inverters["1"]!!.E_Day;
        val producedYear: Double = response.Body.Data.Inverters["1"]!!.E_Year;
        val producedTotal: Double = response.Body.Data.Inverters["1"]!!.E_Total;
        val pvProduction: Double = response.Body.Data.Site.P_PV
        val gridConsumption: Double = response.Body.Data.Site.P_Grid
        val totalConsumption: Double =response.Body.Data.Site.P_Load

        return InstantData(
            producedToday,
            producedYear,
            producedTotal,
            pvProduction,
            gridConsumption,
            totalConsumption,
        )
    }
}

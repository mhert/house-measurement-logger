package inverter

import khttp.get

class HttpBasedInverter(private val inverterBaseUrl: String) : Inverter {
    override fun instantData(): InstantData {
        val result = get("$inverterBaseUrl/solar_api/v1/GetPowerFlowRealtimeData.fcgi").jsonObject
            .getJSONObject("Body")
            .getJSONObject("Data")
            .getJSONObject("Site")

        val producedToday: Double = if (!result.isNull("E_Day")) result.getDouble("E_Day") else 0.0
        val producedYear: Double = if (!result.isNull("E_Year")) result.getDouble("E_Year") else 0.0
        val producedTotal: Double = if (!result.isNull("E_Total")) result.getDouble("E_Total") else 0.0
        val pvProduction: Double = if (!result.isNull("P_PV")) result.getDouble("P_PV") else 0.0
        val gridConsumption: Double = if (!result.isNull("P_Grid")) result.getDouble("P_Grid") else 0.0
        val totalConsumption: Double = if (!result.isNull("P_Load")) result.getDouble("P_Load") else 0.0

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

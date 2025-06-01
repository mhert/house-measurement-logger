package housemeasurementlogger.inverter

import kotlinx.serialization.Serializable

@Serializable
data class HttpBasedInverterResponse(val Body: BodyObject) {
    @Serializable
    data class BodyObject(val Data: DataObject) {
        @Serializable
        data class DataObject(val Inverters: Map<String, InverterObject>, val Site: SiteObject) {
            @Serializable
            data class InverterObject(
                val E_Day: Double?,
                val E_Total: Double?,
                val E_Year: Double?,
            )

            @Serializable
            data class SiteObject(val P_Grid: Double?, val P_Load: Double?, val P_PV: Double?)
        }
    }
}

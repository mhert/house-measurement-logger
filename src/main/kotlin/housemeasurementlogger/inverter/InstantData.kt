package housemeasurementlogger.inverter

class InstantData(
    producedToday: Double,
    producedYear: Double,
    producedTotal: Double,
    pvProduction: Double,
    gridConsumption: Double,
    totalConsumption: Double,
) {
    private val dataDouble =
        mapOf(
            "producedToday" to producedToday,
            "producedYear" to producedYear,
            "producedTotal" to producedTotal,
            "pvProduction" to pvProduction,
            "gridConsumption" to gridConsumption,
            "totalConsumption" to totalConsumption,
        )

    fun getDouble(datasetFieldName: String): Double {
        return this.dataDouble.get(datasetFieldName)!!
    }
}

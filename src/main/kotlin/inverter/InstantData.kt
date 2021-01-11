package inverter

data class InstantData(
    val producedToday: Double,
    val producedYear: Double,
    val producedTotal: Double,
    val pvProduction: Double,
    val gridConsumption: Double,
    val totalConsumption: Double,
)

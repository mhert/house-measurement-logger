package heatpump

data class InstantData(
    val outsideTemperature: Double,
    val outgoingTemperature: Double,
    val returnTemperature: Double,
    val waterTemperatureBottom: Double,
    val waterTemperatureTop: Double
)

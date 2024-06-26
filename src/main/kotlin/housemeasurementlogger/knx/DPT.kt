package housemeasurementlogger.knx

enum class DPT(val id: String, val unit: String) {
    PERCENT("5.001", "%"),
    TEMPERATURE("9.001", "°C"),
    INTENSITY_OF_LIGHT("9.004", "lx"),
    WIND_SPEED("9.005", "m/s");

    companion object {
        fun fromTypeId(typeId: String): DPT {
            return when (typeId) {
                PERCENT.id -> PERCENT
                TEMPERATURE.id -> TEMPERATURE
                INTENSITY_OF_LIGHT.id -> INTENSITY_OF_LIGHT
                WIND_SPEED.id -> WIND_SPEED
                else -> {
                    throw RuntimeException("Type $typeId not implemented")
                }
            }
        }
    }
}

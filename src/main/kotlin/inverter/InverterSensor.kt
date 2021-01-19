package inverter

class InverterSensor(
    val name: String,
    val datasetFieldName: String,
    val type: Type,
)  {
    enum class Type(val type: String) {
        TYPE_DOUBLE("DOUBLE");

        companion object {
            fun fromType(type: String): Type {
                return when (type) {
                    TYPE_DOUBLE.type -> TYPE_DOUBLE
                    else -> {
                        throw RuntimeException("Type $type not implemented for InverterSensor.Type")
                    }
                }
            }
        }
    }
}

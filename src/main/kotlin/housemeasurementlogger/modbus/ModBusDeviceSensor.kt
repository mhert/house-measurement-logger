package housemeasurementlogger.modbus

import java.util.*

class ModBusDeviceSensor(
    val id: UUID,
    val name: String,
    val unit: Int,
    val register: Int,
    val length: Int,
    val type: Type,
) {
    enum class Type(val type: String) {
        TYPE_DOUBLE("DOUBLE");

        companion object {
            fun fromType(type: String): Type {
                return when (type) {
                    TYPE_DOUBLE.type -> TYPE_DOUBLE
                    else -> {
                        throw RuntimeException(
                            "Type $type not implemented for ModBusDeviceSensor.Type"
                        )
                    }
                }
            }
        }
    }
}

package housemeasurementlogger.knx

class TwoByteFloatValue(val type: DPT, val value: Double) {
    override fun toString(): String {
        return value.toString()
    }

    fun toDouble(): Double {
        return value
    }
}

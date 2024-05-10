package housemeasurementlogger.knx

class OneByteIntValue(val type: DPT, val value: Int) {
    override fun toString(): String {
        return value.toString()
    }

    fun toInt(): Int {
        return value
    }
}

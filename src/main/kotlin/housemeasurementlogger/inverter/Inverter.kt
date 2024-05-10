package housemeasurementlogger.inverter

interface Inverter {
    suspend fun instantData(): InstantData
}

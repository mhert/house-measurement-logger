package housemeasurementlogger.inverter

interface Inverter {
    suspend fun instantData(): Result<InstantData>
}

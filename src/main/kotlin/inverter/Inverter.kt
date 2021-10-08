package inverter

interface Inverter {
    suspend fun instantData(): InstantData
}

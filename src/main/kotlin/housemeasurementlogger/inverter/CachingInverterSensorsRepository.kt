package housemeasurementlogger.inverter

class CachingInverterSensorsRepository(private val cachedRepository: InverterSensorsRepository) :
    InverterSensorsRepository {
    private lateinit var allSensorsCache: Collection<InverterSensor>

    override fun allSensors(): Collection<InverterSensor> {
        if (!this::allSensorsCache.isInitialized) {
            allSensorsCache = cachedRepository.allSensors()
        }

        return allSensorsCache
    }
}

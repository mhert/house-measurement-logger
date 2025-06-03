package housemeasurementlogger.knx_sensors

class CachingKnxSensorsRepository(private val cachedRepository: KnxSensorsRepository) :
    KnxSensorsRepository {
    private lateinit var allSensorsCache: Collection<KnxSensor>

    override fun allSensors(): Collection<KnxSensor> {
        if (!this::allSensorsCache.isInitialized) {
            allSensorsCache = cachedRepository.allSensors()
        }

        return allSensorsCache
    }
}

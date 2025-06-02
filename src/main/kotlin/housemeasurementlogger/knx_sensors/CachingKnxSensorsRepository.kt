package housemeasurementlogger.knx_sensors

class CachingKnxSensorsRepository(private val cachedKnxSensorsRepository: KnxSensorsRepository) :
    KnxSensorsRepository {
    private lateinit var allSensorsCache: Collection<KnxSensor>

    override fun allSensors(): Collection<KnxSensor> {
        if (!this::allSensorsCache.isInitialized) {
            allSensorsCache = cachedKnxSensorsRepository.allSensors()
        }

        return allSensorsCache
    }
}

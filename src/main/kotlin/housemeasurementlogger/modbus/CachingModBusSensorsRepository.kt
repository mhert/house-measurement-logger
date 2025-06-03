package housemeasurementlogger.modbus

class CachingModBusSensorsRepository(private val cachedRepository: ModBusSensorsRepository) :
    ModBusSensorsRepository {
    private lateinit var allSensorsCache: Collection<ModBusDeviceSensor>

    override fun allSensors(): Collection<ModBusDeviceSensor> {
        if (!this::allSensorsCache.isInitialized) {
            allSensorsCache = cachedRepository.allSensors()
        }

        return allSensorsCache
    }
}

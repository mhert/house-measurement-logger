package measurements

import com.github.jershell.kbson.BsonEncoder
import com.mongodb.client.MongoDatabase
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import org.litote.kmongo.getCollectionOfName
import org.litote.kmongo.serialization.registerSerializer
import java.time.Instant
import java.util.*

class MongoMeasurementRepository(val db: MongoDatabase) : MeasurementRepository {

    private val serializer = object : KSerializer<Measurement> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Measurement") {
            element<String>("sensorId")
            element<String>("sensorName")
            element<String>("measurementDate")
            element<String>("value")
        }

        override fun deserialize(decoder: Decoder): Measurement =
            decoder.decodeStructure(descriptor) {
                var sensorId = ""
                var sensorName = ""
                var measurementDate = ""
                var value = 0.0
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> sensorId = decodeStringElement(descriptor, 0)
                        1 -> sensorName = decodeStringElement(descriptor, 0)
                        2 -> measurementDate = decodeStringElement(descriptor, 1)
                        3 -> value = decodeDoubleElement(descriptor, 2)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
                return Measurement(
                    UUID.fromString(sensorId),
                    sensorName,
                    Instant.parse(measurementDate),
                    value
                )
            }

        override fun serialize(encoder: Encoder, value: Measurement) {
            if (encoder !is BsonEncoder) {
                throw RuntimeException("encoder must be of type BsonEncoder")
            }

            encoder.encodeStructure(descriptor) {
                if (encoder.encodeElement(descriptor, 0)) encoder.encodeUUID(value.sensorId)
                encodeStringElement(descriptor, 1, value.sensorName)
                if (encoder.encodeElement(descriptor, 2)) encoder.encodeDateTime(value.measurementDate.toEpochMilli())
                encodeDoubleElement(descriptor, 3, value.value)
            }
        }
    }

    init {
        registerSerializer(serializer)
    }

    override fun addMeasurement(measurement: Measurement) {
        db.getCollectionOfName<Measurement>("measurements").insertOne(measurement)
    }

    override fun allMeasurements(): MeasurementCollection {
        TODO("Not yet implemented")
    }
}

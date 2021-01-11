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

class MongoMeasurementRepository(val db: MongoDatabase) : MeasurementRepository {

    private val serializer = object : KSerializer<Measurement> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Measurement") {
            element<String>("sensorName")
            element<String>("measurementDate")
            element<String>("value")
        }

        override fun deserialize(decoder: Decoder): Measurement =
            decoder.decodeStructure(descriptor) {
                var sensorName = ""
                var measurementDate = ""
                var value = 0.0
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> sensorName = decodeStringElement(descriptor, 0)
                        1 -> measurementDate = decodeStringElement(descriptor, 1)
                        2 -> value = decodeDoubleElement(descriptor, 2)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
                Measurement(sensorName, Instant.parse(measurementDate), value)
            }

        override fun serialize(encoder: Encoder, value: Measurement) {
            if (encoder !is BsonEncoder) {
                throw RuntimeException("encoder must be of type BsonEncoder")
            }

            encoder.encodeStructure(descriptor) {
                encodeStringElement(descriptor, 0, value.sensorName)
                if (encoder.encodeElement(descriptor, 1)) encoder.encodeDateTime(value.measurementDate.toEpochMilli())
                encodeDoubleElement(descriptor, 2, value.value)
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

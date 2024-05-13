package housemeasurementlogger.knx

/** 3-level group address */
class GroupAddress private constructor(private val groupAddress: Int) {
    companion object {
        fun fromString(groupAddress: String): GroupAddress {
            val (mainGroup, middleGroup, subGroup) = groupAddress.split('/').map { it.toInt() }

            assertValidMainGroup(mainGroup)
            assertValidMiddleGroup(middleGroup)
            assertValidSubGroup(subGroup)
            assertGroupAddressNotZero(mainGroup, middleGroup, subGroup)

            return GroupAddress((mainGroup shl 11) or (middleGroup shl 8) or (subGroup))
        }

        fun fromInt(groupAddress: Int): GroupAddress {
            return GroupAddress(groupAddress)
        }

        private fun assertGroupAddressNotZero(mainGroup: Int, middleGroup: Int, subGroup: Int) {
            if (mainGroup + middleGroup + subGroup == 0) {
                throw RuntimeException("Group Address must not be 0/0/0")
            }
        }

        private fun assertValidMainGroup(mainGroup: Int) {
            if (mainGroup < 0 || mainGroup > 31) {
                throw RuntimeException("MainGroup must be a value of 0 - 31")
            }
        }

        private fun assertValidMiddleGroup(middleGroup: Int) {
            if (middleGroup < 0 || middleGroup > 7) {
                throw RuntimeException("MiddleGroup must be a value of 0 - 7")
            }
        }

        private fun assertValidSubGroup(subGroup: Int) {
            if (subGroup < 0 || subGroup > 255) {
                throw RuntimeException("SubGroup must be a value of 0 - 255")
            }
        }
    }

    fun mainGroup(): Byte {
        return (groupAddress ushr 11 and 0x1F).toByte()
    }

    fun middleGroup(): Byte {
        return (groupAddress ushr 8 and 0x07).toByte()
    }

    fun subGroup(): Byte {
        return (groupAddress and 0xFF).toByte()
    }

    override fun toString(): String {
        return "${this.mainGroup()}/${this.middleGroup()}/${this.subGroup()}"
    }

    fun toInt(): Int {
        return groupAddress
    }

    override fun equals(other: Any?): Boolean {
        return other is GroupAddress && this.groupAddress == other.groupAddress
    }

    override fun hashCode(): Int {
        return groupAddress
    }
}

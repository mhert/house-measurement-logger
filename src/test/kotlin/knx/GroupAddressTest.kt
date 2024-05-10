package knx

import housemeasurementlogger.knx.GroupAddress
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class GroupAddressTest {

    @Test
    fun `mainGroup should return proper value when group address was created from string`() {
        val groupAddress = GroupAddress.fromString("1/2/3")

        Assertions.assertEquals(1, groupAddress.mainGroup())
    }

    @Test
    fun `middleGroup should return proper value when group address was created from string`() {
        val groupAddress = GroupAddress.fromString("1/2/3")

        Assertions.assertEquals(2, groupAddress.middleGroup())
    }

    @Test
    fun `subGroup should return proper value when group address was created from string`() {
        val groupAddress = GroupAddress.fromString("1/2/3")

        Assertions.assertEquals(3, groupAddress.subGroup())
    }
}

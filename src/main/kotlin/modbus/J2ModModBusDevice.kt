package modbus

import com.ghgande.j2mod.modbus.facade.ModbusTCPMaster
import java.nio.ByteBuffer

class J2ModModBusDevice(
    private val modbus: ModbusTCPMaster
): ModBusDevice {

    init {
        modbus.connect()
    }

    override fun getDoubleDataForSensor(sensor: ModBusDeviceSensor): Double {
        return readFloatRegister(sensor.unit, sensor.register, sensor.length).toDouble()
    }

    private fun readFloatRegister(unitId: Int, ref: Int, count: Int): Float {
        return ByteBuffer.wrap(
            modbus.readMultipleRegisters(unitId, ref, count)
                .reversedArray()
                .flatMap { it.toBytes().asIterable() }
                .toByteArray()
        ).float;
    }
}
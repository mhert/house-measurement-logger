package heatpump

import com.ghgande.j2mod.modbus.facade.ModbusTCPMaster
import java.nio.ByteBuffer

class ModBusBasedHeatPump(
    private val modbus: ModbusTCPMaster
): HeatPump {

    init {
        modbus.connect()
    }

    override fun instantData(): InstantData {
        return InstantData(
            outsideTemperature = readFloatRegister(1, 1002, 2).toDouble(),
            outgoingTemperature = readFloatRegister(1, 1050, 2).toDouble(),
            returnTemperature = readFloatRegister(1, 1052, 2).toDouble(),
            waterTemperatureBottom = readFloatRegister(1, 1012, 2).toDouble(),
            waterTemperatureTop = readFloatRegister(1, 1014, 2).toDouble(),
        )
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
package de.zebrajaeger.phserver.hardware;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * 'Inspired' by <a href="https://github.com/wollewald/INA219_WE/blob/master/src/INA219_WE.h">https://github.com/wollewald/INA219_WE/blob/master/src/INA219_WE.h</a>
 * Datasheet: <a href="https://www.ti.com/lit/ds/symlink/ina219.pdf">https://www.ti.com/lit/ds/symlink/ina219.pdf</a>
 */
public class PowerGaugeDeviceIna219 implements PowerGauge {
    private final HardwareDevice hardwareDevice;
    private static final int SHUNT_RESISTOR_VALUE_IN_MILLIOHM = 100;

    public PowerGaugeDeviceIna219(HardwareDevice hardwareDevice) {
        this.hardwareDevice = hardwareDevice;
          /* Set ADC Mode for Bus and ShuntVoltage
  * Mode *            * Res / Samples *       * Conversion Time *
  BIT_MODE_9        9 Bit Resolution             84 µs
  BIT_MODE_10       10 Bit Resolution            148 µs
  BIT_MODE_11       11 Bit Resolution            276 µs
  BIT_MODE_12       12 Bit Resolution            532 µs  (DEFAULT)
  SAMPLE_MODE_2     Mean Value 2 samples         1.06 ms
  SAMPLE_MODE_4     Mean Value 4 samples         2.13 ms
  SAMPLE_MODE_8     Mean Value 8 samples         4.26 ms
  SAMPLE_MODE_16    Mean Value 16 samples        8.51 ms
  SAMPLE_MODE_32    Mean Value 32 samples        17.02 ms
  SAMPLE_MODE_64    Mean Value 64 samples        34.05 ms
  SAMPLE_MODE_128   Mean Value 128 samples       68.10 ms
  */

    }

    @Override
    public int readVoltageInMillivolt() throws IOException {
        final int v = readRegister((byte) 2);
        // TODO check (v&0x01)==0 ; if false there is a voltage overflow
        return (v >>3) * 4;
    }

    @Override
    public int readCurrentInMilliampere() throws IOException {
        int x =  readRegister((byte) 1) & 0x7fff; // 1 LB = 10μV
        // TODO check for negative Voltage (https://www.ti.com/lit/ds/symlink/ina219.pdf Page 21)
        x *= 10; // to μV
        x = (x * 1000) / SHUNT_RESISTOR_VALUE_IN_MILLIOHM; // to μA
        return x / 1000; // in mA
    }

    @Override
    public int readPower() throws IOException {
        return readVoltageInMillivolt() * readCurrentInMilliampere() / 1000;
    }


    @SuppressWarnings("unused")
    private void writeRegister(short value) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(value);
        hardwareDevice.write(buffer.array());
    }

    private int readRegister(byte adr) throws IOException {
        hardwareDevice.write(new byte[]{adr});
        ByteBuffer buffer = ByteBuffer.wrap(hardwareDevice.read(2)).order(ByteOrder.BIG_ENDIAN);
        return 0xffff & buffer.getShort();
    }
}

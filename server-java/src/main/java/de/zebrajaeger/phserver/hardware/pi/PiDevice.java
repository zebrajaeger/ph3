package de.zebrajaeger.phserver.hardware.pi;

import com.pi4j.io.i2c.I2CDevice;
import de.zebrajaeger.phserver.hardware.HardwareDevice;

import java.io.IOException;

public class PiDevice implements HardwareDevice {
    private final I2CDevice i2CDevice;

    public PiDevice(I2CDevice i2CDevice) {
        this.i2CDevice = i2CDevice;
    }

    @Override
    public byte[] read(int count) throws IOException {
        byte[] result = new byte[count];
        i2CDevice.read(result, 0, count);
        return result;
    }

    @Override
    public void write(byte[] data) throws IOException {
        i2CDevice.write(data, 0, data.length);
    }
}

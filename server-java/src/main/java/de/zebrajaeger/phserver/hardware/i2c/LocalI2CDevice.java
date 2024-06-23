package de.zebrajaeger.phserver.hardware.i2c;

import java.io.IOException;

public class LocalI2CDevice implements I2CDevice {
    private final com.pi4j.io.i2c.I2CDevice i2CDevice;

    public LocalI2CDevice(com.pi4j.io.i2c.I2CDevice i2CDevice) {
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

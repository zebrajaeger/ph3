package de.zebrajaeger.phserver.hardware.i2c;

import java.io.IOException;

public interface I2CDevice {
    byte[] read(int count) throws IOException;
    void write(byte[] data) throws IOException;
}

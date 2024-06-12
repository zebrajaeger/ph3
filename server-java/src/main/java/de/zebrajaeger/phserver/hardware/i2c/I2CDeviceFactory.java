package de.zebrajaeger.phserver.hardware.i2c;

public interface I2CDeviceFactory {
    I2CDevice create(int i2cAddress) throws Exception;
}

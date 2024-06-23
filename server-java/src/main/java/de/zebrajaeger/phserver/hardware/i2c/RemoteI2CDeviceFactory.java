package de.zebrajaeger.phserver.hardware.i2c;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("remotei2c")
@Service
public class RemoteI2CDeviceFactory implements I2CDeviceFactory {
    @Value("${i2c.remote.host}")
    private String host;

    @Override
    public I2CDevice create(int i2cAddress) throws Exception {
        return new RemoteI2CDevice(host, i2cAddress);
    }
}

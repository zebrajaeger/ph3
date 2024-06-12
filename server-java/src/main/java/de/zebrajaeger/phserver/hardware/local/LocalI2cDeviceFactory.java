package de.zebrajaeger.phserver.hardware.local;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.util.Console;
import de.zebrajaeger.phserver.hardware.i2c.I2CDevice;
import de.zebrajaeger.phserver.hardware.i2c.I2CDeviceFactory;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;

@Profile({"locali2c"})
@Service
@Slf4j
public class LocalI2cDeviceFactory implements I2CDeviceFactory {
    @Value("${i2c.bus:0x01}")
    private int i2cBusNumber;
    final Console console = new Console();
    private I2CBus bus;

    @PostConstruct
    private void init() throws IOException, I2CFactory.UnsupportedBusNumberException {
        int[] ids = I2CFactory.getBusIds();
        console.println("Found follow I2C busses: " + Arrays.toString(ids));
        bus = I2CFactory.getInstance(i2cBusNumber);
        scan(bus);
    }

    public I2CDevice create(int i2cAddress) throws IOException {
        return new LocalI2CDevice(bus.getDevice(i2cAddress));
    }

    private void scan(I2CBus bus) {
        log.info(String.format("Scan Bus: %d", bus.getBusNumber()));
        log.info("     0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 128; i++) {
            String iAsHex = String.format("%02X", i);
            if (i % 16 == 0) {
                log.info(sb.toString());
                sb.setLength(0);
                sb.append(iAsHex).append(": ");
            }

            if (i > 0) {
                try {
                    com.pi4j.io.i2c.I2CDevice device = bus.getDevice(i);
                    device.write((byte) 0);
                    sb.append(iAsHex).append(" ");
                } catch (Exception ignore) {
                    sb.append("-- ");
                }
            } else {
                sb.append("   ");
            }
        }
        log.info(sb.toString());
    }
}

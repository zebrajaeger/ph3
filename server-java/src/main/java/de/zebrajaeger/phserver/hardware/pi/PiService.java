package de.zebrajaeger.phserver.hardware.pi;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.util.Console;
import de.zebrajaeger.phserver.hardware.*;
import de.zebrajaeger.phserver.hardware.remote.RemoteService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;

@Service
@Profile({"pi", "default"})
public class PiService implements HardwareService {
    private static final Logger LOG = LoggerFactory.getLogger(RemoteService.class);

    /**
     * #define I2C_ADDRESS 0x33
     */
    @Value("${i2c.address.panohead:0x33}")
    private int i2cPanoHeadAddress;
    @Value("${i2c.address.ina219:0x40}")
    private int i2cIna219Address;
    @Value("${i2c.address.adxl345:0x53}")
    private int i2cAccelerationSensorAddress;

    private PanoHead panoHead;
    private PowerGauge powerGauge;
    private AccelerationSensor accelerationSensor;

    private PiSystem piSystem;

    @PostConstruct
    public void init() throws IOException, I2CFactory.UnsupportedBusNumberException {
        final Console console = new Console();
        int[] ids = I2CFactory.getBusIds();
        console.println("Found follow I2C busses: " + Arrays.toString(ids));
        I2CBus bus = I2CFactory.getInstance(1);
        scan(bus);
        panoHead = new PanoHeadDevice(new PiDevice(bus.getDevice(i2cPanoHeadAddress)));
        powerGauge = new PowerGaugeDeviceIna219(new PiDevice(bus.getDevice(i2cIna219Address)));
        accelerationSensor = new AccelerationSensorDeviceAdxl345(new PiDevice(bus.getDevice(i2cAccelerationSensorAddress)));
        piSystem = new PiSystem();
    }

    @Override
    public Optional<PanoHead> getPanoHead() {
        return Optional.of(panoHead);
    }

    @Override
    public Optional<PowerGauge> getPowerGauge() {
        return Optional.of(powerGauge);
    }

    @Override
    public Optional<AccelerationSensor> getAccelerationSensor() {
        return Optional.of(accelerationSensor);
    }

    @Override
    public Optional<SystemDevice> getSystemDevice() {
        return Optional.empty();
    }

    private void scan(I2CBus bus) {
        LOG.info(String.format("Scan Bus: %d", bus.getBusNumber()));
        LOG.info("     0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 128; i++) {
            String iAsHex = String.format("%02X", i);
            if (i % 16 == 0) {
                LOG.info(sb.toString());
                sb.setLength(0);
                sb.append(iAsHex).append(": ");
            }

            if (i > 0) {
                try {
                    I2CDevice device = bus.getDevice(i);
                    device.write((byte) 0);
                    sb.append(iAsHex).append(" ");
                } catch (Exception ignore) {
                    sb.append("-- ");
                }
            } else {
                sb.append("   ");
            }
        }
        LOG.info(sb.toString());
    }
}

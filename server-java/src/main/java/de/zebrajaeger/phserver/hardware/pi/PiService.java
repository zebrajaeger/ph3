package de.zebrajaeger.phserver.hardware.pi;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.util.Console;
import de.zebrajaeger.phserver.hardware.HardwareService;
import de.zebrajaeger.phserver.hardware.Joystick;
import de.zebrajaeger.phserver.hardware.JoystickDevice;
import de.zebrajaeger.phserver.hardware.PanoHead;
import de.zebrajaeger.phserver.hardware.PanoHeadDevice;
import de.zebrajaeger.phserver.hardware.remote.RemoteService;
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

    @Value("${i2c.address.joystick:0x31}")
    private int i2cJoystickAddress;
    @Value("${i2c.address.panohead:0x30}")
    private int i2cPanoHeadAddress;

    private Joystick joystick;
    private PanoHead panoHead;

    @PostConstruct
    public void init() throws IOException, I2CFactory.UnsupportedBusNumberException {
        final Console console = new Console();
        int[] ids = I2CFactory.getBusIds();
        console.println("Found follow I2C busses: " + Arrays.toString(ids));
        I2CBus bus = I2CFactory.getInstance(1);
        scan(bus);
        joystick = new JoystickDevice(new PiDevice(bus.getDevice(i2cJoystickAddress)));
        panoHead = new PanoHeadDevice(new PiDevice(bus.getDevice(i2cPanoHeadAddress)));
    }

    @Override
    public Joystick getJoystick() {
        return joystick;
    }

    @Override
    public PanoHead getPanoHead() {
        return panoHead;
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

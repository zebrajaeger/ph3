package de.zebrajaeger.phserver.hardware;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.util.Console;
import de.zebrajaeger.phserver.HardwareService;
import de.zebrajaeger.phserver.Joystick;
import de.zebrajaeger.phserver.PanoHead;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;

@Service
@Profile({"pi", "default"})
public class I2cService implements HardwareService {

    private final I2CBus bus;
    private final I2cJoystick joystick;
    private final I2cPanoHead panoHead;

    public I2cService() throws IOException, I2CFactory.UnsupportedBusNumberException {
        final Console console = new Console();
        int[] ids = I2CFactory.getBusIds();
        console.println("Found follow I2C busses: " + Arrays.toString(ids));
        bus = I2CFactory.getInstance(1);
        I2cUtils.scan(bus);
        joystick = new I2cJoystick(bus.getDevice(0x30));
        panoHead = new I2cPanoHead(bus.getDevice(0x31));

//            for (; ; ) {
//                I2cJoystick.Position position = joystick.read();
//                I2cController.Data data = controller.read();
//                System.out.println(position);
//                System.out.println(data);
//                Thread.sleep(1000);
//            }
    }

    public Joystick getJoystick() {
        return joystick;
    }

    public I2CBus getBus() {
        return bus;
    }

    @Override
    public PanoHead getPanoHead() {
        return panoHead;
    }
}

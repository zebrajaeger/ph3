package de.zebrajaeger.phserver.hardware;

import com.pi4j.io.i2c.I2CDevice;
import de.zebrajaeger.phserver.Joystick;
import de.zebrajaeger.phserver.data.RawPosition;

import java.io.IOException;
import java.nio.ByteBuffer;

public class I2cJoystick implements Joystick {
    private final I2CDevice i2CDevice;

    public I2cJoystick(I2CDevice i2CDevice) {
        this.i2CDevice = i2CDevice;
    }

    public RawPosition read() throws IOException {
        ByteBuffer joystickBuffer = ByteBuffer.allocate(4);
        i2CDevice.read(joystickBuffer.array(), 0, joystickBuffer.capacity());
        int x = joystickBuffer.getShort();
        int y = joystickBuffer.getShort();
        return new RawPosition(x, y);
    }
}

package de.zebrajaeger.phserver.hardware.joystick;

import de.zebrajaeger.phserver.data.RawPosition;
import de.zebrajaeger.phserver.hardware.i2c.I2CDevice;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class I2CJoystick {
    private final I2CDevice i2CDevice;

    public I2CJoystick(I2CDevice i2CDevice) {
        this.i2CDevice = i2CDevice;
    }

    public RawPosition read() throws IOException {
        ByteBuffer data = ByteBuffer.wrap(i2CDevice.read(4)).order(ByteOrder.LITTLE_ENDIAN);
        int x = data.getShort();
        int y = data.getShort();
        return new RawPosition(x, y);
    }
}
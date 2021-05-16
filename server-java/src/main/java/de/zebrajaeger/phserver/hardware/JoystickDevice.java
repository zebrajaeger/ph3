package de.zebrajaeger.phserver.hardware;

import de.zebrajaeger.phserver.data.RawPosition;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class JoystickDevice implements Joystick{
    private final HardwareDevice hardwareDevice;

    public JoystickDevice(HardwareDevice hardwareDevice) {
        this.hardwareDevice = hardwareDevice;
    }

    public RawPosition read() throws IOException {
        ByteBuffer data = ByteBuffer.wrap(hardwareDevice.read(4)).order(ByteOrder.LITTLE_ENDIAN);
        int x = data.getShort();
        int y = data.getShort();
        return new RawPosition(x, y);
    }
}

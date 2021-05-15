package de.zebrajaeger.phserver.fakehardware;

import de.zebrajaeger.phserver.Joystick;
import de.zebrajaeger.phserver.data.RawPosition;

public class FakeJoystick implements Joystick {

    public void reset() {

    }

    @Override
    public RawPosition read() {
        return new RawPosition((int) (Math.random() * 100) - 50, (int) (Math.random() * 100) - 50);
    }
}

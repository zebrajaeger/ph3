package de.zebrajaeger.phserver.hardware.fake;

import de.zebrajaeger.phserver.hardware.Joystick;
import de.zebrajaeger.phserver.data.RawPosition;

public class FakeJoystick implements Joystick {

    public void reset() {

    }

    @Override
    public RawPosition read() {
        return new RawPosition((int) (Math.random() * 100) - 50, (int) (Math.random() * 100) - 50);
    }
}

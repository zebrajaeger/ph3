package de.zebrajaeger.phserver.hardware;

import de.zebrajaeger.phserver.data.RawPosition;

import java.io.IOException;

public interface Joystick {
    RawPosition read() throws IOException;
}

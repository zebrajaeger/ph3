package de.zebrajaeger.phserver.hardware;

import java.io.IOException;

public interface HardwareDevice {
    byte[] read(int count) throws IOException;
    void write(byte[] data) throws IOException;
}

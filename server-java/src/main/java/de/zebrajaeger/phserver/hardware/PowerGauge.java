package de.zebrajaeger.phserver.hardware;

import java.io.IOException;

public interface PowerGauge {
    int readVoltageInMillivolt() throws IOException;
    int readCurrentInMilliampere() throws IOException ;
    int readPower() throws IOException ;
}

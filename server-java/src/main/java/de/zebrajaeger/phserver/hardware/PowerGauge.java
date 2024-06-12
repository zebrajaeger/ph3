package de.zebrajaeger.phserver.hardware;

import java.io.IOException;

@Deprecated
public interface PowerGauge {
    int readVoltageInMillivolt() throws IOException;
    int readCurrentInMilliampere() throws IOException ;
}

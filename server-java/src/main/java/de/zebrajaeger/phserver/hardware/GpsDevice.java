package de.zebrajaeger.phserver.hardware;

import de.zebrajaeger.phserver.data.GpsData;

import java.io.IOException;
@Deprecated
public interface GpsDevice {
    GpsData read() throws IOException;
}

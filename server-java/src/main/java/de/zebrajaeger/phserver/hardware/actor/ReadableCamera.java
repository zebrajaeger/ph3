package de.zebrajaeger.phserver.hardware.actor;

import de.zebrajaeger.phserver.data.CameraStatus;

import java.io.IOException;

public interface ReadableCamera{
    void update() throws IOException;
    CameraStatus readCameraStatus();
}

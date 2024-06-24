package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.data.CameraStatus;

public record CameraChangedEvent(CameraStatus cameraStatus) {

}

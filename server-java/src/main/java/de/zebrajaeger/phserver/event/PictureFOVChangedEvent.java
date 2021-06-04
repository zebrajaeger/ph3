package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.data.FieldOfView;

public class PictureFOVChangedEvent {
    private final FieldOfView pictureFOV;

    public PictureFOVChangedEvent(FieldOfView pictureFOV) {
        this.pictureFOV = pictureFOV;
    }

    public FieldOfView getPictureFOV() {
        return pictureFOV;
    }
}

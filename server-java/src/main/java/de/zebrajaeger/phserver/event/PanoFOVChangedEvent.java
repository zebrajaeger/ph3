package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.data.FieldOfViewPartial;

public class PanoFOVChangedEvent {
    private final FieldOfViewPartial panoFOV;

    public PanoFOVChangedEvent(FieldOfViewPartial panoFOV) {
        this.panoFOV = panoFOV;
    }

    public FieldOfViewPartial getPanoFOV() {
        return panoFOV;
    }
}

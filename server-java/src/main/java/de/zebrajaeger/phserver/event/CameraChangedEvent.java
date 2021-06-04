package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.data.Camera;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CameraChangedEvent {
    private final Camera camera;

    public CameraChangedEvent(Camera camera) {
        this.camera = camera;
    }

    public Camera getCamera() {
        return camera;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}

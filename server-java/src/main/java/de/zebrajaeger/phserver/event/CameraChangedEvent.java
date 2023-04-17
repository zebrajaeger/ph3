package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.data.Camera;
import lombok.Data;

@Data
public class CameraChangedEvent {

  private final Camera camera;
}

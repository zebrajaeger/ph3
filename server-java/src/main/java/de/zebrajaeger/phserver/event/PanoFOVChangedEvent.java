package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.data.FieldOfViewPartial;
import lombok.Data;

@Data
public class PanoFOVChangedEvent {

  private final FieldOfViewPartial panoFOV;
}

package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.data.FieldOfView;
import lombok.Data;

@Data
public class PictureFOVChangedEvent {

  private final FieldOfView pictureFOV;
}

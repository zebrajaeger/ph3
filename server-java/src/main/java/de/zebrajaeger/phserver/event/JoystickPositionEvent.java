package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.data.Position;
import lombok.Data;

@Data
public class JoystickPositionEvent {

  private final Position position;
}

package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.data.Position;
import de.zebrajaeger.phserver.data.RawPosition;
import lombok.Data;

@Data
public class PositionEvent {

  private final RawPosition rawPosition;
  private final Position position;
}

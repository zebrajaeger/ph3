package de.zebrajaeger.phserver.event;

import lombok.Data;

@Data
public class OverlapChangedEvent {

  private final double minimumOverlapH;
  private final double minimumOverlapV;
}

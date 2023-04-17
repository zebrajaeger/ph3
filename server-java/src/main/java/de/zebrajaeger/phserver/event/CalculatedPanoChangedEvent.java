package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.data.CalculatedPano;
import lombok.Data;

@Data
public class CalculatedPanoChangedEvent {

  private final CalculatedPano calculatedPano;
}

package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.data.Power;
import lombok.Data;

@Data
public class PowerMeasureEvent {

  private final Power power;
}

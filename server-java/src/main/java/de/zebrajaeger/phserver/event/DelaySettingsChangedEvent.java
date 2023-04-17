package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.data.Delay;
import lombok.Data;

@Data
public class DelaySettingsChangedEvent {

  private final Delay delay;
}

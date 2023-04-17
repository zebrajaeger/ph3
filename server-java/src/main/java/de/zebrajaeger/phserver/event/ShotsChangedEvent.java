package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.data.Shots;
import lombok.Data;

@Data
public class ShotsChangedEvent {

  private final Shots shots;
}

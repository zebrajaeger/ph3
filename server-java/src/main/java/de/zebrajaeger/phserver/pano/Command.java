package de.zebrajaeger.phserver.pano;

import de.zebrajaeger.phserver.data.ShotPosition;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class Command {

  private final ShotPosition shotPosition;
  private final String description;
}

package de.zebrajaeger.phserver.pano;

import de.zebrajaeger.phserver.data.ShotPosition;
import lombok.Getter;

@Getter
public class WaitCommand extends Command {

  private final int timeMs;

  public WaitCommand(ShotPosition shotPosition, String description, int timeMs) {
    super(shotPosition, description);
    this.timeMs = timeMs;
  }
}

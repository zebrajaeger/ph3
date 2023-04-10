package de.zebrajaeger.phserver.pano;

import de.zebrajaeger.phserver.data.Shot;
import de.zebrajaeger.phserver.data.ShotPosition;
import lombok.Getter;

@Getter
public class TakeShotCommand extends Command {

  private final Shot shot;

  public TakeShotCommand(ShotPosition shotPosition, String description, Shot shot) {
    super(shotPosition, description);
    this.shot = shot;
  }
}

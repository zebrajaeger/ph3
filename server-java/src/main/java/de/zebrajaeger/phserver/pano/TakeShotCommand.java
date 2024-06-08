package de.zebrajaeger.phserver.pano;

import de.zebrajaeger.phserver.data.ShotPosition;
import de.zebrajaeger.phserver.settings.ShotSettings;
import lombok.Getter;

@Getter
public class TakeShotCommand extends Command {

  private final ShotSettings shot;
  private final Integer id;

  public TakeShotCommand(ShotPosition shotPosition, String description, ShotSettings shot, Integer id) {
    super(shotPosition, description);
    this.shot = shot;
    this.id = id;
  }
}

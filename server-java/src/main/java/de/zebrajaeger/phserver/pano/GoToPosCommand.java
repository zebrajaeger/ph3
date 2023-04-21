package de.zebrajaeger.phserver.pano;

import de.zebrajaeger.phserver.data.ShotPosition;

public class GoToPosCommand extends Command {

  public GoToPosCommand(ShotPosition shotPosition, String description) {
    super(shotPosition, description);
  }
}

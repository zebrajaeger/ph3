package de.zebrajaeger.phserver.pano;

import de.zebrajaeger.phserver.data.ShotPosition;

public class NormalizePositionCommand extends Command {

  public NormalizePositionCommand(ShotPosition shotPosition, String description) {
    super(shotPosition, description);
  }
}

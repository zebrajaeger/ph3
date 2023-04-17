package de.zebrajaeger.phserver.pano;

import de.zebrajaeger.phserver.data.ShotPosition;

public class ApplyOffsetCommand extends Command {

  public ApplyOffsetCommand(ShotPosition shotPosition, String description) {
    super(shotPosition, description);
  }
}

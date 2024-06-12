package de.zebrajaeger.phserver.data;

import lombok.Data;

@Data
public class PanoHeadData {

  protected ActorStatus actorStatus = new ActorStatus();
  protected Camera camera = new Camera();
  protected int movementRaw;
  protected int cameraRaw;

  public void init() {
    actorStatus.x.setAtTargetPos((movementRaw & 0x01) != 0);
    actorStatus.x.setMoving((movementRaw & 0x02) != 0);

    actorStatus.y.setAtTargetPos((movementRaw & 0x04) != 0);
    actorStatus.y.setMoving((movementRaw & 0x08) != 0);

    actorStatus.z.setAtTargetPos((movementRaw & 0x10) != 0);
    actorStatus.z.setMoving((movementRaw & 0x20) != 0);

    camera.focus = ((cameraRaw & 0x01) != 0);

    camera.trigger = ((cameraRaw & 0x02) != 0);
  }
}

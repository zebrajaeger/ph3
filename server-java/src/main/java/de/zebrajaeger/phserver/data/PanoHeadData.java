package de.zebrajaeger.phserver.data;

import lombok.Data;

@Data
public class PanoHeadData {

  protected Actor actor = new Actor();
  protected Camera camera = new Camera();
  protected int movementRaw;
  protected int cameraRaw;

  public void init() {
    actor.x.setAtTargetPos((movementRaw & 0x01) != 0);
    actor.x.setMoving((movementRaw & 0x02) != 0);
    actor.y.setAtTargetPos((movementRaw & 0x04) != 0);
    actor.y.setMoving((movementRaw & 0x08) != 0);
    camera.focus = ((cameraRaw & 0x01) != 0);
    camera.trigger = ((cameraRaw & 0x02) != 0);
  }
}

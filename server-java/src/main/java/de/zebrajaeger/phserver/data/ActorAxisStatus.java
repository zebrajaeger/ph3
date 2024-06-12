package de.zebrajaeger.phserver.data;

import lombok.Data;

@Data
public class ActorAxisStatus {

  private int pos = 0;
  private int speed = 0;
  private boolean isMoving = false;
  private boolean atTargetPos = false;
}

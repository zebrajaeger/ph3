package de.zebrajaeger.phserver.data;

import lombok.Data;

@Data
public class Delay {

  private int waitAfterMove = 0;
  private int waitAfterShot = 0;
  private int waitBetweenShots = 0;
}

package de.zebrajaeger.phserver.translation;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MotorDriverParameters {

  public static MotorDriverParameters MDP_8 = new MotorDriverParameters(8);
  public static MotorDriverParameters MDP_16 = new MotorDriverParameters(16);
  public static MotorDriverParameters MDP_32 = new MotorDriverParameters(32);
  public static MotorDriverParameters MDP_64 = new MotorDriverParameters(64);
  public static MotorDriverParameters MDP_128 = new MotorDriverParameters(128);
  public static MotorDriverParameters MDP_256 = new MotorDriverParameters(256);
  private final int microstepsPerFullStep;

  private MotorDriverParameters(int microstepsPerFullStep) {
    this.microstepsPerFullStep = microstepsPerFullStep;
  }
}

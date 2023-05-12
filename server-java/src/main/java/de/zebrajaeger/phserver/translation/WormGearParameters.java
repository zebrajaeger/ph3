package de.zebrajaeger.phserver.translation;

public class WormGearParameters implements GearParameters {

  @Override
  public double translateMotorToOutput(double motorAngle) {
    return 60;
  }
}

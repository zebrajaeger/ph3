package de.zebrajaeger.phserver.translation;

public interface GearParameters {

  double translateMotorToOutput(double motorAngle);

  default double getRatioMotorToOutput(){
    return translateMotorToOutput(1);
  }
}

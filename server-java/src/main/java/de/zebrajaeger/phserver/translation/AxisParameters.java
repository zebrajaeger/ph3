package de.zebrajaeger.phserver.translation;

import lombok.Getter;

@Getter
public class AxisParameters {

  private final MotorParameters motorParameters;
  private final MotorDriverParameters motorDriverParameters;
  private final GearParameters gearParameters;

  private final double ratio;
  private final int maxStepFrequency;
  private final int maxAccelerationFrequency;
  private final boolean inverted;

  public AxisParameters(MotorParameters motorParameters,
      MotorDriverParameters motorDriverParameters,
      GearParameters gearParameters, boolean inverted) {
    this.motorParameters = motorParameters;
    this.motorDriverParameters = motorDriverParameters;
    this.gearParameters = gearParameters;
      this.inverted = inverted;

      ratio = motorParameters.getStepsPerRevolution()
        * motorDriverParameters.getMicrostepsPerFullStep()
        * gearParameters.outputToMotor(1);

    maxStepFrequency = calculateMaxStepFrequency();

    maxAccelerationFrequency = calculateMaxAccelerationFrequency();
  }

  public double rawToDeg(int rawValue) {
    return rawValue * 360d / ratio;
  }

  public int degToRaw(double degValue) {
    return (int) ((degValue / 360d) * ratio);
  }

  private int calculateMaxStepFrequency() {
    return (int) ((motorParameters.getMaxRPM() / 60d)
        * motorParameters.getStepsPerRevolution()
        * motorDriverParameters.getMicrostepsPerFullStep());
  }

  private int calculateMaxAccelerationFrequency() {
    return calculateMaxStepFrequency() * 2; // ca 1/2s to max speed
  }
}

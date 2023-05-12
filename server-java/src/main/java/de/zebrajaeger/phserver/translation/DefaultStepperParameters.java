package de.zebrajaeger.phserver.translation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DefaultStepperParameters implements MotorParameters {

  private double maxRPM = 375;

  @Override
  public int getStepsPerRevolution() {
    return 200;
  }
}

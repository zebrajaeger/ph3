package de.zebrajaeger.phserver.hardware.axis;

import de.zebrajaeger.phserver.data.AxisIndex;
import de.zebrajaeger.phserver.hardware.actor.Actor;
import de.zebrajaeger.phserver.translation.AxisParameters;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Getter
@Slf4j
public class AxisWithOffset implements Axis{

  private final Actor actor;
  private final AxisIndex axisIndex;
  //  private final AxisTranslator axisTranslatorService;
  private final AxisParameters axisParameters;

  private int offsetRaw = 0;
  private int rawValue = 0;

  public AxisWithOffset(Actor actor, AxisIndex axisIndex, AxisParameters axisParameters) {
    this.actor = actor;
    this.axisIndex = axisIndex;
    this.axisParameters = axisParameters;

    try {
      actor.setLimit(
          axisIndex,
          6 * 10,
          axisParameters.getMaxStepFrequency(),
          axisParameters.getMaxAccelerationFrequency());
    } catch (Exception e) {
      log.error("could not set limit for axisIndex: {}", axisIndex, e);
    }
  }

  /**
   * @param posDeg Position to go to.
   * @return true: already at the required position; false: move required;
   */
  public boolean moveTo(double posDeg) throws Exception {
    if (axisParameters.isInverted()) {
      posDeg = -posDeg;
    }
    int raw = axisParameters.degToRaw(posDeg);
    final int targetPos = raw - offsetRaw;
    if (rawValue != targetPos) {
      setTargetPosRaw(targetPos);
      return false;
    } else {
      return true;
    }
  }

  public void moveRelative(double angleDeg) throws Exception {
    if (angleDeg == 0d) {
      return;
    }

    if (axisParameters.isInverted()) {
      angleDeg = -angleDeg;
    }
    setTargetPosRaw(rawValue + axisParameters.degToRaw(angleDeg));
  }

  public void adaptOffset() throws Exception {
    if (rawValue != 0) {
      offsetRaw += rawValue;
      actor.setActualAndTargetPos(axisIndex, 0);
    }
  }

  public void normalizeAxisPosition() {
    double a = axisParameters.rawToDeg(rawValue + offsetRaw);
    int revolutions = (int) (a / 360d);
    int rawDelta = axisParameters.degToRaw(360 * revolutions);
    offsetRaw -= rawDelta;
  }

  public void setToZero() throws Exception {
    offsetRaw = 0;
    actor.setActualAndTargetPos(axisIndex, 0);
  }

  /**
   * @param velocity [0..1]
   */
  public void setVelocity(double velocity) throws Exception {
    actor.setTargetVelocity(axisIndex, (int) (velocity * axisParameters.getMaxStepFrequency()));
  }

  public double getDegValue() {
    return axisParameters.isInverted()
        ? -(axisParameters.rawToDeg(rawValue + offsetRaw))
        : axisParameters.rawToDeg(rawValue + offsetRaw);
  }

  private void setTargetPosRaw(int pos) throws Exception {
    actor.setTargetPos(axisIndex, pos);
  }

  public void setRawValue(int rawValue) {
    this.rawValue = rawValue;
  }
}

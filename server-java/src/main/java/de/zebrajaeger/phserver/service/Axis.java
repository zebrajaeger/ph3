package de.zebrajaeger.phserver.service;

import de.zebrajaeger.phserver.hardware.PanoHead;
import de.zebrajaeger.phserver.translation.AxisParameters;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Getter
@Slf4j
public class Axis {

  private final PanoHead panoHead;
  private final int axisIndex;
  //  private final AxisTranslator axisTranslatorService;
  private final AxisParameters axisParameters;

  private final boolean isInverted;
  private int offsetRaw = 0;
  private int rawValue = 0;

  public Axis(PanoHead panoHead, int axisIndex, AxisParameters axisParameters,
      boolean isInverted) {
    this.panoHead = panoHead;
    this.axisIndex = axisIndex;
    this.axisParameters = axisParameters;
    this.isInverted = isInverted;

    try {
      panoHead.setLimit(
          axisIndex,
          6 * 10,
          axisParameters.getMaxStepFrequency(),
          axisParameters.getMaxAccelerationFrequency());
    } catch (IOException e) {
      log.error("could not set limit for axisIndex: {}", axisIndex, e);
    }
  }

  /**
   * @param posDeg Position to go to.
   * @return true: already at the required position; false: move required;
   */
  public boolean moveTo(double posDeg) throws IOException {
    if (isInverted) {
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

  public void moveRelative(double angleDeg) throws IOException {
    if (angleDeg == 0d) {
      return;
    }

    if (isInverted) {
      angleDeg = -angleDeg;
    }
    setTargetPosRaw(rawValue + axisParameters.degToRaw(angleDeg));
  }

  public void adaptOffset() throws IOException {
    if (rawValue != 0) {
      offsetRaw += rawValue;
      panoHead.setActualAndTargetPos(axisIndex, 0);
    }
  }

  public void normalizeAxisPosition() {
    double a = axisParameters.rawToDeg(rawValue + offsetRaw);
    int revolutions = (int) (a / 360d);
    int rawDelta = axisParameters.degToRaw(360 * revolutions);
    offsetRaw -= rawDelta;
  }

  public void setToZero() throws IOException {
    offsetRaw = 0;
    panoHead.setActualAndTargetPos(axisIndex, 0);
  }

  /**
   * @param velocity [0..1]
   */
  public void setVelocity(double velocity) throws IOException {
    panoHead.setTargetVelocity(axisIndex, (int) (velocity * axisParameters.getMaxStepFrequency()));
  }

  public double getDegValue() {
    return isInverted
        ? -(axisParameters.rawToDeg(rawValue + offsetRaw))
        : axisParameters.rawToDeg(rawValue + offsetRaw);
  }

  private void setTargetPosRaw(int pos) throws IOException {
    panoHead.setTargetPos(axisIndex, pos);
  }

  public void setRawValue(int rawValue) {
    this.rawValue = rawValue;
  }
}

package de.zebrajaeger.phserver.service;

import de.zebrajaeger.phserver.hardware.PanoHead;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Axis {

  private final PanoHead panoHead;
  private final int axisIndex;
  private final AxisTranslatorService axisTranslatorService;

  private final boolean isInverted;
  private int offsetRaw = 0;
  private int rawValue = 0;

  public Axis(PanoHead panoHead, int axisIndex, AxisTranslatorService axisTranslatorService,
      boolean isInverted) {
    this.panoHead = panoHead;
    this.axisIndex = axisIndex;
    this.axisTranslatorService = axisTranslatorService;
    this.isInverted = isInverted;
  }

  /**
   *
   * @param posDeg Position to go to.
   * @return  true: already at the required position; false: move required;
   */
  public boolean moveTo(double posDeg) throws IOException {
    if (isInverted) {
      posDeg = -posDeg;
    }
    int raw = axisTranslatorService.degToRaw(posDeg);
    final int targetPos = raw - offsetRaw;
    if(rawValue!=targetPos){
      setTargetPosRaw(targetPos);
      return false;
    }else{
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
    setTargetPosRaw(rawValue + axisTranslatorService.degToRaw(angleDeg));
  }

  public void adaptOffset() throws IOException {
    if (rawValue != 0) {
      offsetRaw += rawValue;
      panoHead.setActualAndTargetPos(axisIndex, 0);
    }
  }

  public void normalizeAxisPosition() {
    double a = axisTranslatorService.rawToDeg(rawValue + offsetRaw);
    int revolutions = (int) (a / 360d);
    int rawDelta = axisTranslatorService.degToRaw(360 * revolutions);
    offsetRaw -= rawDelta;
  }

  public void setToZero() throws IOException {
    offsetRaw = 0;
    panoHead.setActualAndTargetPos(axisIndex, 0);
  }

  public void setVelocityRaw(int velocity) throws IOException {
    panoHead.setTargetVelocity(axisIndex, rawValue);
  }

  public void setVelocityDeg(int velocity) throws IOException {
    panoHead.setTargetVelocity(axisIndex, axisTranslatorService.degToRaw(velocity));
  }

  public double getDegValue() {
    return isInverted
        ? -(axisTranslatorService.rawToDeg(rawValue + offsetRaw))
        : axisTranslatorService.rawToDeg(rawValue + offsetRaw);
  }

  private void setTargetPosRaw(int pos) throws IOException {
    panoHead.setTargetPos(axisIndex, pos);
  }

  public void setRawValue(int rawValue) {
    this.rawValue = rawValue;
  }
}

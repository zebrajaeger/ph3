package de.zebrajaeger.phserver.service;

import org.springframework.stereotype.Service;

@Service
public class AxisTranslatorServiceImpl implements AxisTranslatorService {

  // https://www.ti.com/lit/ds/symlink/drv8825.pdf
  // https://www.pololu.com/product/2133/pictures#lightbox-picture0J4222
  // DRV8825: 8; TMC2209: 16
  private static final double STEPPER_MICROSTEPS = 16;
  // https://www.omc-stepperonline.com/download/11HS12-0674D-PG27.pdf
  private static final double STEPPER_STEPS = 200;
  private static final double STEPPER_GEARBOX_RATIO = 26d + (103d / 121d);
  // 3D Printed Gearbox 15:75 = 1:5
  private static final double GEARBOX_RATIO = 75d / 15d;

  protected static double RATIO =
      STEPPER_STEPS * STEPPER_MICROSTEPS * STEPPER_GEARBOX_RATIO * GEARBOX_RATIO;

  public double rawToDeg(int rawValue) {
    return rawValue * 360d / RATIO;
  }

  public int degToRaw(double degValue) {
    return (int) ((degValue / 360d) * RATIO);
  }
}

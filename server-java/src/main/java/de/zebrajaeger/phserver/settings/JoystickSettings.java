package de.zebrajaeger.phserver.settings;

import lombok.Data;

@Data
public class JoystickSettings implements SettingsValue<JoystickSettings>{

  private double cutBorder;
  private JoystickAxisSettings x = new JoystickAxisSettings();
  private JoystickAxisSettings y = new JoystickAxisSettings();

  @Override
  public void read(JoystickSettings value) {
    value.setCutBorder(cutBorder);
    value.getX().write(x);
    value.getY().write(y);
  }

  @Override
  public void write(JoystickSettings value) {
    cutBorder = value.getCutBorder();
    x.read(value.getX());
    y.read(value.getY());
  }
}

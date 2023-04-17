package de.zebrajaeger.phserver.settings;

import lombok.Data;

@Data
public class JoystickSettings {

  private double cutBorder;
  private JoystickAxisSettings x = new JoystickAxisSettings();
  private JoystickAxisSettings y = new JoystickAxisSettings();
}

package de.zebrajaeger.phserver.papywizard.settings;

import lombok.Data;

@Data
public class JoystickSettings {

  private double cutBorder;
  private JoystickAxisSettings x = new JoystickAxisSettings();
  private JoystickAxisSettings y = new JoystickAxisSettings();
}

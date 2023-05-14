package de.zebrajaeger.phserver.papywizard.settings;

import lombok.Data;

@Data
public class Settings {

  private JoystickSettings joystick = new JoystickSettings();
  private FovSettings pictureFov = new FovSettings();
  private FovSettings panoFov = new FovSettings();
  private ShotsSettings shots = new ShotsSettings();
  private DelaySettings delay = new DelaySettings();
}

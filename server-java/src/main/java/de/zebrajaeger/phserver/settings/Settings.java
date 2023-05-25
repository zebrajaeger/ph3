package de.zebrajaeger.phserver.settings;

import lombok.Data;

@Data
public class Settings {

  private JoystickSettings joystick = new JoystickSettings();
  private FovSettings pictureFov = new FovSettings();
  private FovSettings panoFov = new FovSettings();
  private PicturePresetsSettings picturePresets = new PicturePresetsSettings();
  private ShotsSettings shots = new ShotsSettings();
  private DelaySettings delay = new DelaySettings();
  private PanoSettings pano = new PanoSettings();
}
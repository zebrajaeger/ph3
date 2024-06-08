package de.zebrajaeger.phserver.settings;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Settings implements SettingsValue<Settings> {

    private JoystickSettings joystick = new JoystickSettings();
    private CameraFovSettings pictureFov = new CameraFovSettings();
    private PanoFovSettings panoFov = new PanoFovSettings();
    private ShotsSettings shots = new ShotsSettings();
    private DelaySettings delay = new DelaySettings();
    private PanoSettings pano = new PanoSettings();

    @Override
    public void read(Settings value) {
        joystick.read(value.getJoystick());
        pictureFov.read(value.getPictureFov());
        panoFov.read(value.getPanoFov());
        shots.read(value.getShots());
        delay.read(value.getDelay());
        pano.read(value.getPano());
    }

    @Override
    public void write(Settings value) {
        value.getJoystick().read(joystick);
        value.getPictureFov().read(pictureFov);
        value.getPanoFov().read(panoFov);
        value.getShots().read(shots);
        value.getDelay().read(delay);
        value.getPano().read(pano);
    }
}

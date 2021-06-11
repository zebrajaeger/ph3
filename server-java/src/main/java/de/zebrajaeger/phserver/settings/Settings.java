package de.zebrajaeger.phserver.settings;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class Settings {
    private JoystickSettings joystick = new JoystickSettings();
    private FovSettings pictureFov = new FovSettings();
    private FovSettings panoFov = new FovSettings();
    private ShotsSettings shots = new ShotsSettings();
    private DelaySettings delay = new DelaySettings();

    //<editor-fold desc="boilerplate">
    public JoystickSettings getJoystick() {
        return joystick;
    }

    public void setJoystick(JoystickSettings joystick) {
        this.joystick = joystick;
    }

    public FovSettings getPictureFov() {
        return pictureFov;
    }

    public void setPictureFov(FovSettings pictureFov) {
        this.pictureFov = pictureFov;
    }

    public ShotsSettings getShots() {
        return shots;
    }

    public void setShots(ShotsSettings shots) {
        this.shots = shots;
    }

    public DelaySettings getDelay() {
        return delay;
    }

    public void setDelay(DelaySettings delaySettings) {
        this.delay = delaySettings;
    }

    public FovSettings getPanoFov() {
        return panoFov;
    }

    public void setPanoFov(FovSettings panoFov) {
        this.panoFov = panoFov;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
    //</editor-fold>
}

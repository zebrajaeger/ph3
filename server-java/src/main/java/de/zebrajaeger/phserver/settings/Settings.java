package de.zebrajaeger.phserver.settings;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class Settings {
    private JoystickSettings joystick = new JoystickSettings();
    private FovSettings pictureFov = new FovSettings();
    private ShotsSettings shots = new ShotsSettings();
    private DelaySettings delaySettings = new DelaySettings();

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
        return delaySettings;
    }

    public void setDelay(DelaySettings delaySettings) {
        this.delaySettings = delaySettings;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
    //</editor-fold>
}

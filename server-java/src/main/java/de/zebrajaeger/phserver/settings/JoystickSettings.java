package de.zebrajaeger.phserver.settings;

import de.zebrajaeger.phserver.data.JoystickPosition;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class JoystickSettings {
    private double cutBorder;
    private JoystickAxisSettings x = new JoystickAxisSettings();
    private JoystickAxisSettings y = new JoystickAxisSettings();

    public void setAll(JoystickPosition position) {
        x.setAll(position.getX());
        y.setAll(position.getY());
    }
    public void getAll(JoystickPosition position) {
        x.getAll(position.getX());
        y.getAll(position.getY());
    }

    public double getCutBorder() {
        return cutBorder;
    }

    public void setCutBorder(double cutBorder) {
        this.cutBorder = cutBorder;
    }

    public JoystickAxisSettings getX() {
        return x;
    }

    public void setX(JoystickAxisSettings x) {
        this.x = x;
    }

    public JoystickAxisSettings getY() {
        return y;
    }

    public void setY(JoystickAxisSettings y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}

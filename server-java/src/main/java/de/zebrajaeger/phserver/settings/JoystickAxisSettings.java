package de.zebrajaeger.phserver.settings;

import de.zebrajaeger.phserver.data.JoystickAxis;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class JoystickAxisSettings {
    private float rawMin = Float.MAX_VALUE;
    private float rawCenter = 0f;
    private float rawMax = -Float.MAX_VALUE;

    public float getRawMin() {
        return rawMin;
    }

    public void setRawMin(float rawMin) {
        this.rawMin = rawMin;
    }

    public float getRawCenter() {
        return rawCenter;
    }

    public void setRawCenter(float rawCenter) {
        this.rawCenter = rawCenter;
    }

    public float getRawMax() {
        return rawMax;
    }

    public void setRawMax(float rawMax) {
        this.rawMax = rawMax;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    public void setAll(JoystickAxis axis) {
        rawMin = axis.getAutoRange().getRawMin();
        rawCenter = axis.getAutoRange().getRawCenter();
        rawMax = axis.getAutoRange().getRawMax();
    }

    public void getAll(JoystickAxis axis) {
        axis.getAutoRange().setAll(rawMin, rawCenter, rawMax);
    }
}

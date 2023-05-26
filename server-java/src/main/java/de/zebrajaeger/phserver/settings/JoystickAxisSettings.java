package de.zebrajaeger.phserver.settings;

import lombok.Data;

@Data
public class JoystickAxisSettings implements SettingsValue<JoystickAxisSettings> {

    private float rawMin = Float.MAX_VALUE;
    private float rawCenter = 0f;
    private float rawMax = -Float.MAX_VALUE;

    @Override
    public void read(JoystickAxisSettings value) {
        value.setRawMin(rawMin);
        value.setRawCenter(rawCenter);
        value.setRawMax(rawMax);
    }

    @Override
    public void write(JoystickAxisSettings value) {
        rawMin = value.getRawMin();
        rawCenter = value.getRawCenter();
        rawMax = value.getRawMax();
    }
}

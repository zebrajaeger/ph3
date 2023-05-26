package de.zebrajaeger.phserver.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShotSettings implements SettingsValue<ShotSettings> {

    private int focusTimeMs = 1000;
    private int triggerTimeMs = 1000;

    @Override
    public void read(ShotSettings value) {
        value.setFocusTimeMs(focusTimeMs);
        value.setTriggerTimeMs(triggerTimeMs);
    }

    @Override
    public void write(ShotSettings value) {
        focusTimeMs = value.getFocusTimeMs();
        triggerTimeMs = value.getTriggerTimeMs();
    }
}

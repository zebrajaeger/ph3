package de.zebrajaeger.phserver.settings;

import java.util.HashMap;

public class ShotsPresetSettings extends HashMap<String, ShotsSettings> implements SettingsValue<ShotsPresetSettings> {

    public void putCopy(String name, ShotsSettings value) {
        final ShotsSettings n = new ShotsSettings();
        n.write(value);
        put(name, n);
    }

    @Override
    public void read(ShotsPresetSettings value) {
        replace(this, value);
    }

    @Override
    public void write(ShotsPresetSettings value) {
        replace(value, this);
    }

    private void replace(ShotsPresetSettings from, ShotsPresetSettings to) {
        to.clear();
        for (ShotsPresetSettings.Entry<String, ShotsSettings> f : from.entrySet()) {
            to.putCopy(f.getKey(), f.getValue());
        }
    }
}

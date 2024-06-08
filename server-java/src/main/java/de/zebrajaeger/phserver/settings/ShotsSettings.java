package de.zebrajaeger.phserver.settings;

import java.util.ArrayList;

public class ShotsSettings extends ArrayList<ShotSettings> implements SettingsValue<ShotsSettings> {

    public void addCopy(ShotSettings shotSettings){
        final ShotSettings n = new ShotSettings();
        n.write(shotSettings);
        add(n);
    }

    @Override
    public void read(ShotsSettings value) {
        replace(this, value);
    }

    @Override
    public void write(ShotsSettings value) {
        replace(value, this);
    }

    private void replace(ShotsSettings from, ShotsSettings to) {
        to.clear();
        for (ShotSettings f : from) {
            to.addCopy(f);
        }
    }
}

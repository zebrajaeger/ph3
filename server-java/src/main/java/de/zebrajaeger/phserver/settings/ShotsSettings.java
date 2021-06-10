package de.zebrajaeger.phserver.settings;

import de.zebrajaeger.phserver.data.Shot;
import de.zebrajaeger.phserver.data.Shots;

import java.util.HashMap;

public class ShotsSettings extends HashMap<String, ShotSetting> {

    public void setAll(Shots shots) {
        clear();
        for (Shots.Entry<String, Shot> e : shots.entrySet()) {
            put(e.getKey(), new ShotSetting(e.getValue()));
        }
    }

    public void getAll(Shots shots) {
        shots.clear();
        for (Shots.Entry<String, ShotSetting> e : entrySet()) {
            shots.put(e.getKey(), e.getValue().getAll(new Shot()));
        }
    }
}

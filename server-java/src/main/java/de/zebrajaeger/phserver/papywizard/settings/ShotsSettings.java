package de.zebrajaeger.phserver.papywizard.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.zebrajaeger.phserver.data.Shot;
import de.zebrajaeger.phserver.data.Shots;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShotsSettings extends HashMap<String, List<ShotSetting>> {

    @JsonIgnore
    public void setDefaultShot(ShotSetting shot) {
        List<ShotSetting> def = get("default");
        if (def == null) {
            def = new ArrayList<>();
            put("default", def);
        }

        def.clear();
        def.add(shot);
    }

    @JsonIgnore
    public boolean isDefaultShotOk() {
        List<ShotSetting> def = get("default");
        if (def == null) {
            return false;
        }
        return def.size() == 1;
    }

    @JsonIgnore
    public void setAll(Shots shots) {
        clear();
        for (Shots.Entry<String, List<Shot>> e : shots.entrySet()) {
            List<ShotSetting> shotList = new ArrayList<>();
            for (Shot s : e.getValue()) {
                shotList.add(new ShotSetting(s));
            }
            put(e.getKey(), shotList);
        }
    }

    @JsonIgnore
    public void getAll(Shots shots) {
        shots.clear();
        for (ShotsSettings.Entry<String, List<ShotSetting>> e : entrySet()) {
            List<Shot> shotList = new ArrayList<>();
            for (ShotSetting s : e.getValue()) {
                shotList.add(s.getAll(new Shot()));
            }
            shots.put(e.getKey(), shotList);
        }
    }
}

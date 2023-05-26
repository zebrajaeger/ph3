package de.zebrajaeger.phserver.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShotsSettings extends HashMap<String, List<ShotSettings>> implements SettingsValue<ShotsSettings> {

    @JsonIgnore
    public void setDefaultShot(ShotSettings shot) {
        List<ShotSettings> def = get("default");
        if (def == null) {
            def = new ArrayList<>();
            put("default", def);
        }

        def.clear();
        def.add(shot);
    }

    @JsonIgnore
    public boolean isDefaultShotOk() {
        List<ShotSettings> def = get("default");
        if (def == null) {
            return false;
        }
        return def.size() == 1;
    }

    @JsonIgnore
    public ShotSettings getShot(String shotsName, int shotIndex) {
        List<ShotSettings> shotList = get(shotsName);
        if (shotList == null || shotIndex >= shotList.size()) {
            return null;
        }
        return shotList.get(shotIndex);
    }

    @JsonIgnore
    public void add(String shotsName, ShotSettings shot) {
        getCreateShotList(shotsName).add(shot);
    }

    @JsonIgnore
    private List<ShotSettings> getCreateShotList(String shotsName) {
        List<ShotSettings> shotList = get(shotsName);
        if (shotList == null) {
            shotList = new ArrayList<>();
            put(shotsName, shotList);
        }
        return shotList;
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
        for (ShotsSettings.Entry<String, List<ShotSettings>> e : from.entrySet()) {
            List<ShotSettings> shotList = new ArrayList<>();
            for (ShotSettings s : e.getValue()) {
                ShotSettings x = new ShotSettings();
                x.read(s);
                shotList.add(s);
            }
            to.put(e.getKey(), shotList);
        }
    }
}

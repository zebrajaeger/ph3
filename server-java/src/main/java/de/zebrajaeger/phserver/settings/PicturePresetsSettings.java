package de.zebrajaeger.phserver.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;
import java.util.TreeMap;

public class PicturePresetsSettings extends TreeMap<String, CameraFovSettings> implements SettingsValue<PicturePresetsSettings> {

    @JsonIgnore
    private void replaceByCopy(Map<String, CameraFovSettings> from, Map<String, CameraFovSettings> to) {
        to.clear();
        from.forEach((name, fov) -> to.put(name, new CameraFovSettings(fov.getX(), fov.getY())));
    }

    @Override
    public void read(PicturePresetsSettings value) {
        replaceByCopy(this, value);
    }

    @Override
    public void write(PicturePresetsSettings value) {
        replaceByCopy(value, this);
    }

    @JsonIgnore
    public String[] getNames() {
        return keySet().toArray(new String[0]);
    }
}

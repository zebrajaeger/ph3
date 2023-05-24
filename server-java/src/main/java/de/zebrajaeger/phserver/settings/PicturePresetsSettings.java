package de.zebrajaeger.phserver.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

public class PicturePresetsSettings extends HashMap<String, SimpleFovSettings> {

    @JsonIgnore
    public void getAll(Map<String, SimpleFovSettings> value) {
        replaceByCopy(this, value);
    }

    @JsonIgnore
    public void setAll(Map<String, SimpleFovSettings> value) {
        replaceByCopy(value, this);
    }

    @JsonIgnore
    public void replaceByCopy(Map<String, SimpleFovSettings> from, Map<String, SimpleFovSettings> to) {
        to.clear();
        from.forEach((name, fov) -> to.put(name, new SimpleFovSettings(fov.getX(), fov.getY())));
    }
}

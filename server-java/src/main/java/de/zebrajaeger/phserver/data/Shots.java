package de.zebrajaeger.phserver.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Optional;

public class Shots extends HashMap<String, Shot> {
    public static final String DEFAULT_SHOT_NAME = "default";

    @JsonIgnore
    public Optional<Shot> getStandardShot() {
        return Optional.ofNullable(get(DEFAULT_SHOT_NAME));
    }

    @JsonIgnore
    public void setStandardShot(Shot shot) {
        put(DEFAULT_SHOT_NAME, shot);
    }
}

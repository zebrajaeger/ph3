package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.data.Shot;

import java.util.List;

public class ShotsChangedEvent {
    private final List<Shot> shots;

    public ShotsChangedEvent(List<Shot> shots) {
        this.shots = shots;
    }

    public List<Shot> getShots() {
        return shots;
    }
}

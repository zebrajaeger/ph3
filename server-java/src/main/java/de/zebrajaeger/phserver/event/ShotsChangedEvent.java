package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.data.Shots;

public class ShotsChangedEvent {
    private final Shots shots;

    public ShotsChangedEvent(Shots shots) {
        this.shots = shots;
    }

    public Shots getShots() {
        return shots;
    }
}

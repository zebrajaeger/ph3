package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.data.Power;

public class PowerMeasureEvent {
    private final Power power;

    public PowerMeasureEvent(Power power) {
        this.power = power;
    }

    public Power getPower() {
        return power;
    }
}

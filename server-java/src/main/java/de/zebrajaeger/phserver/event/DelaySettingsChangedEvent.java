package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.data.Delay;

public class DelaySettingsChangedEvent {
    private final Delay delay;

    public DelaySettingsChangedEvent(Delay delay) {
        this.delay = delay;
    }

    public Delay getDelay() {
        return delay;
    }
}

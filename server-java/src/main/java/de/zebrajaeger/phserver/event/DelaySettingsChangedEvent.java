package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.data.DelaySettings;

public class DelaySettingsChangedEvent {
    private final DelaySettings delaySettings;

    public DelaySettingsChangedEvent(DelaySettings delaySettings) {
        this.delaySettings = delaySettings;
    }

    public DelaySettings getDelaySettings() {
        return delaySettings;
    }
}

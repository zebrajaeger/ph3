package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.settings.ShotsSettings;

public record ShotsChangedEvent(ShotsSettings shots) {

}

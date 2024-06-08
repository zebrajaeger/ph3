package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.settings.ShotsPresetSettings;

public record ShotsPresetsChangedEvent(ShotsPresetSettings shots) {

}

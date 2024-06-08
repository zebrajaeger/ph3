package de.zebrajaeger.phserver.service;

import de.zebrajaeger.phserver.event.ShotsChangedEvent;
import de.zebrajaeger.phserver.event.ShotsPresetsChangedEvent;
import de.zebrajaeger.phserver.settings.ShotSettings;
import de.zebrajaeger.phserver.settings.ShotsPresetSettings;
import de.zebrajaeger.phserver.settings.ShotsSettings;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@Getter
public class ShotService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final SettingsService settingsService;

    private final ShotsPresetSettings shotPresets = new ShotsPresetSettings();
    private final ShotsSettings current = new ShotsSettings();

    public ShotService(ApplicationEventPublisher applicationEventPublisher,
                       SettingsService settingsService) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.settingsService = settingsService;
    }

    @PostConstruct
    public void init() {
        settingsService.getShotsPresetStore().getSettings().read(shotPresets);
        publishShotPresetChange();

        settingsService.getSettingsStore().getSettings().getShots().read(current);
        if(current.isEmpty()){
            // default value
            current.add(new ShotSettings(500,1000));
        }
        publishShotsChange();
    }

    public void publishShotPresetChange() {
        settingsService.getShotsPresetStore().getSettings().write(shotPresets);
        settingsService.getShotsPresetStore().saveDelayed();
        applicationEventPublisher.publishEvent(new ShotsPresetsChangedEvent(shotPresets));
    }

    public void publishShotsChange() {
        settingsService.getSettingsStore().getSettings().getShots().write(current);
        settingsService.getSettingsStore().saveDelayed();
        applicationEventPublisher.publishEvent(new ShotsChangedEvent(current));
    }
}

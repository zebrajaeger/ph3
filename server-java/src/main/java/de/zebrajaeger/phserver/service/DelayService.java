package de.zebrajaeger.phserver.service;

import de.zebrajaeger.phserver.event.DelaySettingsChangedEvent;
import de.zebrajaeger.phserver.settings.DelaySettings;
import de.zebrajaeger.phserver.settings.Settings;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@Getter
public class DelayService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final SettingsService settingsService;

    private final DelaySettings delay = new DelaySettings();

    public DelayService(ApplicationEventPublisher applicationEventPublisher,
                        SettingsService settingsService) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.settingsService = settingsService;
    }

    @PostConstruct
    public void init() {
        final Settings settings = settingsService.getSettingsStore().getSettings();

        settings.getDelay().read(delay);
        publishDelayChange();

    }

    public void publishDelayChange() {
        settingsService.getSettingsStore().getSettings().getDelay().write(delay);
        settingsService.getSettingsStore().saveDelayed();
        applicationEventPublisher.publishEvent(new DelaySettingsChangedEvent(delay));
    }
}

package de.zebrajaeger.phserver.service;

import de.zebrajaeger.phserver.event.CCApiSettingsChangedEvent;
import de.zebrajaeger.phserver.settings.CCApiSettings;
import de.zebrajaeger.phserver.settings.Settings;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@Getter
public class CCApiService {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final SettingsService settingsService;

    private final CCApiSettings ccapi = new CCApiSettings();

    public CCApiService(ApplicationEventPublisher applicationEventPublisher,
                        SettingsService settingsService) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.settingsService = settingsService;
    }

    @PostConstruct
    public void init() {
        final Settings settings = settingsService.getSettingsStore().getSettings();

        settings.getCcapi().read(ccapi);
        publishChange();
    }

    public void publishChange() {
        settingsService.getSettingsStore().getSettings().getCcapi().write(ccapi);
        settingsService.getSettingsStore().saveDelayed();
        applicationEventPublisher.publishEvent(new CCApiSettingsChangedEvent(ccapi));
    }
}

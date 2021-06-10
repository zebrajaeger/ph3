package de.zebrajaeger.phserver;

import de.zebrajaeger.phserver.data.JoystickPosition;
import de.zebrajaeger.phserver.data.RawPosition;
import de.zebrajaeger.phserver.hardware.HardwareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
public class JoystickService {

    private final HardwareService hardwareService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final SettingsService settingsService;
    private final JoystickPosition position = new JoystickPosition();
    private RawPosition rawPosition;

    @Autowired
    public JoystickService(HardwareService hardwareService,
                           ApplicationEventPublisher applicationEventPublisher,
                           SettingsService settingsService) {
        this.hardwareService = hardwareService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.settingsService = settingsService;
    }

    @PostConstruct
    public void init() {
        settingsService.getSettings().getJoystick().getAll(position);
    }

    @Scheduled(initialDelay = 0, fixedRateString = "${joystick.period:25}")
    public void update() throws IOException {
        rawPosition = hardwareService.getJoystick().read();
        position.updateWithRawValues(rawPosition.getX(), rawPosition.getY());
        applicationEventPublisher.publishEvent(position);
    }

    public JoystickPosition getPosition() {
        return position;
    }

    public void reset() throws IOException {
        position.reset();
        settingsService.getSettings().getJoystick().setAll(position);
        settingsService.setDirty();
    }

    public void setCurrentPositionAsCenter() throws IOException {
        position.setCenterWithRawValues(rawPosition.getX(), rawPosition.getY());
        settingsService.getSettings().getJoystick().setAll(position);
        settingsService.setDirty();
    }
}

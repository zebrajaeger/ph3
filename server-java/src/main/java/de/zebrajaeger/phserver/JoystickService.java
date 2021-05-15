package de.zebrajaeger.phserver;

import de.zebrajaeger.phserver.data.JoystickPosition;
import de.zebrajaeger.phserver.data.RawPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class JoystickService {

    private final HardwareService hardwareService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final JoystickPosition position = new JoystickPosition();
    private RawPosition rawPosition;

    @Autowired
    public JoystickService(HardwareService hardwareService, ApplicationEventPublisher applicationEventPublisher) {
        this.hardwareService = hardwareService;
        this.applicationEventPublisher = applicationEventPublisher;
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

    public void reset() {
        position.reset();
    }

    public void setCurrentPositionAsCenter() {
        position.setCenterWithRawValues(rawPosition.getX(), rawPosition.getY());
    }
}

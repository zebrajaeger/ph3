package de.zebrajaeger.phserver.hardware.actor;

import de.zebrajaeger.phserver.data.CameraStatus;
import de.zebrajaeger.phserver.event.CameraChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
public abstract class PollingCamera implements ReadableCamera{
    private final ApplicationEventPublisher applicationEventPublisher;

    public PollingCamera(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Scheduled(initialDelay = 0, fixedRateString = "${controller.main.period:100}")
    public void onUpdate() {
        CameraStatus cameraStatus;
        try {
            update();
            cameraStatus = readCameraStatus();
        } catch (Exception e) {
            log.debug("Could not read data from hardware device", e);
            return;
        }
        applicationEventPublisher.publishEvent(new CameraChangedEvent(cameraStatus));
    }
}

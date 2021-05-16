package de.zebrajaeger.phserver;

import de.zebrajaeger.phserver.data.JoystickPosition;
import de.zebrajaeger.phserver.data.PanoHeadData;
import de.zebrajaeger.phserver.event.JoggingEvent;
import de.zebrajaeger.phserver.event.MovementStoppedEvent;
import de.zebrajaeger.phserver.event.ShotDoneEvent;
import de.zebrajaeger.phserver.hardware.HardwareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PanoHeadService {
    private final HardwareService hardwareService;
    private final ApplicationEventPublisher applicationEventPublisher;
    @Value("${jogging.speed:1000}")
    private float joggingSpeed;

    private boolean jogging;
    private PanoHeadData panoHeadData;

    private final PreviousState previousState = new PreviousState();

    static class PreviousState {
        private boolean cameraActive = false;
        private boolean actorActive = false;

        public boolean isCameraActive() {
            return cameraActive;
        }

        public void setCameraActive(boolean cameraActive) {
            this.cameraActive = cameraActive;
        }

        public boolean isActorActive() {
            return actorActive;
        }

        public void setActorActive(boolean actorActive) {
            this.actorActive = actorActive;
        }
    }

    @Autowired
    public PanoHeadService(HardwareService hardwareService, ApplicationEventPublisher applicationEventPublisher) {
        this.hardwareService = hardwareService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Scheduled(initialDelay = 0, fixedRateString = "${controller.period:100}")
    public void update() throws IOException {

        panoHeadData = hardwareService.getPanoHead().read();

        applicationEventPublisher.publishEvent(panoHeadData);
        // TODO set focus/trigger on start shot
        // TODO same with movement

        boolean cameraActive = panoHeadData.getCamera().isActive();
        boolean actorActive = panoHeadData.getActor().isActive();

        if (previousState.isCameraActive() && !cameraActive) {
            applicationEventPublisher.publishEvent(new ShotDoneEvent());
        }

        if (previousState.isActorActive() && !actorActive) {
            applicationEventPublisher.publishEvent(new MovementStoppedEvent());
        }

        previousState.setCameraActive(cameraActive);
        previousState.setActorActive(actorActive);
    }

    @EventListener
    public void onJoystickPosChanged(JoystickPosition joystickPosition) throws IOException {
        if (isJogging()) {
            hardwareService.getPanoHead().setTargetVelocity(0, (int) (joystickPosition.getX().getCutValue() * joggingSpeed));
            hardwareService.getPanoHead().setTargetVelocity(1, (int) (joystickPosition.getY().getCutValue() * joggingSpeed));
        }
    }

    public PanoHeadData getData() {
        return panoHeadData;
    }

    public boolean isJogging() {
        return jogging;
    }

    public void setJogging(boolean jogging) throws IOException {
        if (jogging == this.jogging) {
            return;
        }

        this.jogging = jogging;
        if (!isJogging()) {
            hardwareService.getPanoHead().setTargetVelocity(0, 0);
            hardwareService.getPanoHead().setTargetVelocity(1, 0);
        }

        applicationEventPublisher.publishEvent(new JoggingEvent(jogging));
    }
}

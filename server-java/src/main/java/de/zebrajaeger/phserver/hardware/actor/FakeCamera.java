package de.zebrajaeger.phserver.hardware.actor;

import de.zebrajaeger.phserver.data.CameraStatus;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Profile({"develop"})
public class FakeCamera extends PollingCamera implements Camera {
    private final CameraStatus cameraStatus = new CameraStatus();
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public FakeCamera(ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
    }

    @Override
    public void startFocus(int focusTimeMs) {
        cameraStatus.setFocus(true);
        executorService.schedule(() -> cameraStatus.setFocus(false), focusTimeMs,
                TimeUnit.MILLISECONDS);
    }

    @Override
    public void startTrigger(int triggerTimeMs) {
        cameraStatus.setTrigger(true);
        executorService.schedule(() -> cameraStatus.setTrigger(false), triggerTimeMs,
                TimeUnit.MILLISECONDS);
    }

    @Override
    public void startShot(int focusTimeMs, int triggerTimeMs) {
        cameraStatus.setFocus(true);
        executorService.schedule(() -> {
            cameraStatus.setFocus(false);
            cameraStatus.setTrigger(true);
            executorService.schedule(() -> cameraStatus.setTrigger(false), triggerTimeMs,
                    TimeUnit.MILLISECONDS);
        }, focusTimeMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public void update() {
        // ignore
    }

    @Override
    public CameraStatus readCameraStatus() {
        return cameraStatus;
    }
}

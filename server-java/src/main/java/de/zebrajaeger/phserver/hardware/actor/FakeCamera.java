package de.zebrajaeger.phserver.hardware.actor;

import de.zebrajaeger.phserver.data.CameraStatus;
import de.zebrajaeger.phserver.event.CameraChangedEvent;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Service
@ConditionalOnProperty("enable.camera.fake")
@Slf4j
public class FakeCamera implements Camera {
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final ApplicationEventPublisher applicationEventPublisher;

    public FakeCamera(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @PostConstruct
    public void init() {
        applicationEventPublisher.publishEvent(new CameraChangedEvent(new CameraStatus(false, false)));
    }

    @Override
    public void startFocus(int focusTimeMs) {
        startFocus(focusTimeMs, null);
    }

    private void startFocus(int focusTimeMs, Consumer<Boolean> onFinish) {
        if (focusTimeMs > 0) {
            log.debug("Camera Focus on");
            applicationEventPublisher.publishEvent(new CameraChangedEvent(new CameraStatus(true, false)));
            executorService.schedule(() -> {
                log.debug("Camera Focus off");
                applicationEventPublisher.publishEvent(new CameraChangedEvent(new CameraStatus(false, false)));
                if (onFinish != null) {
                    onFinish.accept(true);
                }
            }, focusTimeMs, TimeUnit.MILLISECONDS);
        } else {
            if (onFinish != null) {
                onFinish.accept(false);
            }
        }
    }

    @Override
    public void startTrigger(int triggerTimeMs) {
        startTrigger(triggerTimeMs, null);
    }

    private void startTrigger(int triggerTimeMs, Consumer<Boolean> onFinish) {
        if (triggerTimeMs > 0) {
            log.debug("Camera Trigger on");
            applicationEventPublisher.publishEvent(new CameraChangedEvent(new CameraStatus(false, true)));
            executorService.schedule(() -> {
                log.debug("Camera Trigger off");
                applicationEventPublisher.publishEvent(new CameraChangedEvent(new CameraStatus(false, false)));
                if (onFinish != null) {
                    onFinish.accept(true);
                }
            }, triggerTimeMs, TimeUnit.MILLISECONDS);
        } else {
            if (onFinish != null) {
                onFinish.accept(false);
            }
        }
    }

    @Override
    public void startShot(int focusTimeMs, int triggerTimeMs) {
        startFocus(focusTimeMs, x1 -> startTrigger(triggerTimeMs, x2 -> log.debug("Shot done {},{}", x1, x2)));
    }
}

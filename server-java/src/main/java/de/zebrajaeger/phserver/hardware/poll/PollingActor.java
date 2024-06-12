package de.zebrajaeger.phserver.hardware.poll;

import de.zebrajaeger.phserver.data.PanoHeadData;
import de.zebrajaeger.phserver.event.PanoHeadDataEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
public abstract class PollingActor {
    private final ApplicationEventPublisher applicationEventPublisher;

    public PollingActor(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public abstract PanoHeadData read() throws Exception;


    @Scheduled(initialDelay = 0, fixedRateString = "${controller.main.period:100}")
    public void update() {
        PanoHeadData panoHeadData;
        try {
            panoHeadData = read();
        } catch (Exception e) {
            log.debug("Could not read data from hardware device", e);
            return;
        }
        applicationEventPublisher.publishEvent(new PanoHeadDataEvent(panoHeadData));
    }
//        x.setRawValue(panoHeadData.getActor().getByIndex(AxisIndex.X).getPos());
//        y.setRawValue(panoHeadData.getActor().getByIndex(AxisIndex.Y).getPos());
//        applicationEventPublisher.publishEvent(
//                new PositionEvent(getCurrentRawPosition(), getCurrentPosition()));
//
//        // TODO set focus/trigger on start shot
//        // TODO same with movement
//
////    boolean cameraActive = panoHeadData.getCamera().isActive();
//        boolean actorActive = panoHeadData.getActor().isActive();
//
//        Camera camera = panoHeadData.getCamera();
//        if (!camera.equals(previousState.getCamera())) {
//            if (previousState.getCamera() == null || (!camera.isTrigger() && previousState.getCamera()
//                    .isTrigger())) {
//                applicationEventPublisher.publishEvent(new ShotDoneEvent());
//            }
//            previousState.setCamera(new Camera(camera));
//            applicationEventPublisher.publishEvent(new CameraChangedEvent(camera));
//        }
//
//        if (previousState.isActorActive() && !actorActive) {
////      log.info("MOVEMENT STOPPED");
//            applicationEventPublisher.publishEvent(new MovementStoppedEvent());
//        }
//
//        if (previousState.isActorActive() != actorActive) {
//            applicationEventPublisher.publishEvent(new ActorActiveChangedEvent(actorActive));
//        }
//
//        previousState.setActorActive(actorActive);
//    }
}

package de.zebrajaeger.phserver.hardware;

import de.zebrajaeger.phserver.data.*;
import de.zebrajaeger.phserver.event.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

//@Service
//@Slf4j
//@Profile("!mqtt")
@Deprecated
public class PanoHeadValuePollService {
//    private final ApplicationEventPublisher applicationEventPublisher;
//    private final HardwareService hardwareService;
//
////    private PanoHeadData panoHeadData;
////    private final PanoHeadService.PreviousState previousState = new PanoHeadService.PreviousState();
//
//    public PanoHeadValuePollService(ApplicationEventPublisher applicationEventPublisher, HardwareService hardwareService) {
//        this.applicationEventPublisher = applicationEventPublisher;
//        this.hardwareService = hardwareService;
//    }

//    @Scheduled(initialDelay = 1000, fixedRateString = "${controller.power.period:250}")
//    public void updatePowerConsumption() {
//        final PowerGauge powerGauge = hardwareService.getPowerGauge();
//        try {
//            double u = powerGauge.readVoltageInMillivolt() / 1000d;
//            double i = powerGauge.readCurrentInMilliampere() / 1000d;
//            applicationEventPublisher.publishEvent(new PowerMeasureEvent(new Power(u, i)));
//        } catch (IOException e) {
//            log.debug("Could not read PowerGauge");
//        }
//    }

//    @Scheduled(initialDelay = 0, fixedRateString = "${controller.acceleration.period:500}")
//    public void updateAccelerationSensor() throws IOException {
//        hardwareService.getAccelerationSensor().foo();
//    }


//    @Scheduled(initialDelay = 0, fixedRateString = "${controller.main.period:100}")
//    public void update() {
//        PanoHeadData panoHeadData;
//        try {
//            panoHeadData = hardwareService.getPanoHead().read();
//        } catch (IOException e) {
//            log.debug("Could not read data from hardware device", e);
//            return;
//        }
//        applicationEventPublisher.publishEvent(new PanoHeadDataEvent(panoHeadData));

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

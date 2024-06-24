package de.zebrajaeger.phserver.service;

import de.zebrajaeger.phserver.data.*;
import de.zebrajaeger.phserver.event.*;
import de.zebrajaeger.phserver.hardware.actor.Actor;
import de.zebrajaeger.phserver.hardware.actor.Camera;
import de.zebrajaeger.phserver.hardware.axis.Axis;
import de.zebrajaeger.phserver.hardware.battery.BatteryInterpolator;
import de.zebrajaeger.phserver.util.SigmoidCalculator;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PanoHeadService {

    private final Actor actor;
    private final Camera camera;
    private final Axis x;
    private final Axis y;
    private final ApplicationEventPublisher applicationEventPublisher;
    @Getter
    private final LatestState latestState = new LatestState();
    private boolean joggingEnabled;
    private final SigmoidCalculator sigmoid = new SigmoidCalculator();

//    @Getter
//    private PanoHeadData latestPanoHeadData;


    private long lastManualMove = 0;
    private boolean jogByJoystick = false;

    private final BatteryInterpolator batteryInterpolator;

    @Data
    public static class LatestState {
        private ActorStatus actorStatus;
        private CameraStatus cameraStatus;
        private boolean actorActive = false;
    }

    public PanoHeadService(Actor actor, Camera camera,
                           @Qualifier("x") Axis x,
                           @Qualifier("y") Axis y,
                           ApplicationEventPublisher applicationEventPublisher,
                           @Nullable BatteryInterpolator batteryInterpolator) {
        this.actor = actor;
        this.camera = camera;
        this.x = x;
        this.y = y;
        this.applicationEventPublisher = applicationEventPublisher;
        this.batteryInterpolator = batteryInterpolator;
    }

    @EventListener
    public void onPowerMeasureEvent(PowerMeasureEvent powerMeasureEvent) {
        Power power = powerMeasureEvent.power();
        BatteryState batteryState;
        if (power == null || batteryInterpolator == null) {
            batteryState = new BatteryState(false, 0);
        } else {
            batteryState = new BatteryState(true, (int) batteryInterpolator.getPercentForVoltage(power.getVoltage()));
        }
        log.trace("Battery U:{} -> {}%", power != null ? power.getVoltage() : null, batteryState);
        applicationEventPublisher.publishEvent(new BatteryStateEvent(batteryState));
    }

    @EventListener
    public void onCameraStatusChanged(CameraChangedEvent e) {
        CameraStatus cameraStatus = e.cameraStatus();
        if (!cameraStatus.equals(latestState.getCameraStatus())) {
            if (latestState.getCameraStatus() == null || (!cameraStatus.isTrigger() && latestState.getCameraStatus()
                    .isTrigger())) {
                applicationEventPublisher.publishEvent(new ShotDoneEvent());
            }
            latestState.setCameraStatus(new CameraStatus(cameraStatus));
            applicationEventPublisher.publishEvent(new CameraChangedEvent(cameraStatus));
        }
    }

    @EventListener
    public void onActorStatusEvent(ActorStatusEvent event) {
        ActorStatus actorStatus = event.status();

        latestState.setActorStatus(actorStatus);
        x.setRawValue(actorStatus.getByIndex(AxisIndex.X).getPos());
        y.setRawValue(actorStatus.getByIndex(AxisIndex.Y).getPos());
        applicationEventPublisher.publishEvent(new PositionEvent(getCurrentRawPosition(), getCurrentPosition()));

        // TODO set focus/trigger on start shot
        // TODO same with movement

        boolean actorActive = actorStatus.isActive();

        if (latestState.isActorActive() && !actorActive) {
//      log.info("MOVEMENT STOPPED");
            applicationEventPublisher.publishEvent(new MovementStoppedEvent());
        }

        if (latestState.isActorActive() != actorActive) {
            applicationEventPublisher.publishEvent(new ActorActiveChangedEvent(actorActive));
        }

        latestState.setActorActive(actorActive);
    }

    public Position getCurrentPosition() {
        return new Position(x.getDegValue(), y.getDegValue());
    }

    public RawPosition getCurrentRawPosition() {
        return new RawPosition(x.getRawValue(), y.getRawValue());
    }


    //    public boolean isJoggingEnabled() {
//        return joggingEnabled;
//    }

    public void setToZero() {
        try {
            x.setToZero();
            y.setToZero();
        } catch (Exception e) {
            log.debug("Could not set zu zero", e);
        }
    }

    /**
     * Also publishes JoggingChangedEvent
     */
    public void setJoggingEnabled(boolean joggingEnabled) {
        if (joggingEnabled == this.joggingEnabled) {
            return;
        }

        this.joggingEnabled = joggingEnabled;

        // if jogging is disabled now, we stop the movement
        if (!joggingEnabled) {
            stopAll();
        }

        applicationEventPublisher.publishEvent(new JoggingChangedEvent(joggingEnabled));
    }

    /**
     * Move the head for the given degrees, based on current Position
     *
     * @param relPosition way to move in deg
     */
    public void manualRelativeMove(Position relPosition) {
        if (!joggingEnabled) {
            return;
        }

        try {
            x.moveRelative(relPosition.getX());
            y.moveRelative(relPosition.getY());
        } catch (Exception e) {
            log.debug("Could not move", e);
        }
    }

    public void manualAbsoluteMove(Position position) {
        if (!joggingEnabled) {
            return;
        }
        try {
            x.moveTo(position.getX());
            y.moveTo(position.getY());
        } catch (Exception e) {
            log.debug("Could not move", e);
        }
    }

    /**
     * @param position to go to
     * @return true: already at the required position; false: move required
     */
    public boolean goTo(Position position) throws Exception {
        if (joggingEnabled) {
            return false;
        }
        boolean _x = x.moveTo(position.getX());
        boolean _y = y.moveTo(position.getY());
        return _x && _y;
    }

    public void stopAll() {
        try {
            actor.stopAll();
        } catch (Exception e) {
            log.debug("Could stop all", e);
        }
    }

    public void shot(int focusTimeMs, int triggerTimeMs) throws Exception {
        camera.startShot(focusTimeMs, triggerTimeMs);
    }

    public void adaptAxisOffset() {
        try {
            x.adaptOffset();
            y.adaptOffset();
        } catch (Exception e) {
            log.debug("Could not adapt axis", e);
        }
    }

    public void normalizeAxisPosition() {
        x.normalizeAxisPosition();
        y.normalizeAxisPosition();
    }

    @EventListener
    public void onJoystickPosChanged(JoystickPositionEvent event) {
//        manualMoveByJoystick(event.position());
        if (!joggingEnabled) {
            return;
        }

        setSigmoidSpeed(event.position());
    }

    @EventListener
    public void onJoystickConnectionChanged(JoystickConnectionEvent joystickConnectionEvent) {
        if (!joystickConnectionEvent.connected()) {
            stopAll();
        }
    }

//    public void manualMoveByJoystick(Position joystickPosition) {
//        if (!joggingEnabled) {
//            return;
//        }
//
//        setSigmoidSpeed(joystickPosition);
//    }

    public void manualMoveByJoystickWithEmergencyStopOnTimeout(Position joystickPosition) {
        if (!joggingEnabled) {
            return;
        }

        jogByJoystick = true;

        // reset watchdog timeout
        lastManualMove = System.currentTimeMillis();
        setSigmoidSpeed(joystickPosition);
    }

    public void manualMoveByJoystickStop() {
        if (!joggingEnabled) {
            return;
        }

        jogByJoystick = false;

        stopAll();
    }

    @Scheduled(fixedDelay = 100)
    public void checkJoystickMovementTimeout() {
        long now = System.currentTimeMillis();

        // emergency stop ?
        if (jogByJoystick && now - lastManualMove > 200) {
            jogByJoystick = false;
            log.warn("Emergency stop. Reason: Joystick event timeout");
            stopAll();
        }
    }

    private void setSigmoidSpeed(Position speed) {
        Position speed1 = speed.withBorderOfOne();
        try {
            x.setVelocity(sigmoid.value(speed1.getX()));
            y.setVelocity(sigmoid.value(speed1.getY()));
        } catch (Exception e) {
            log.debug("Could not set velocity", e);
        }
    }

    public Position getCurrentPositionDeg() {
        return new Position(x.getDegValue(), y.getDegValue());
    }
}

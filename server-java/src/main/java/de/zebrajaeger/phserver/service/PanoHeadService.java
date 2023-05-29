package de.zebrajaeger.phserver.service;

import de.zebrajaeger.phserver.data.*;
import de.zebrajaeger.phserver.event.*;
import de.zebrajaeger.phserver.hardware.HardwareService;
import de.zebrajaeger.phserver.hardware.PowerGauge;
import de.zebrajaeger.phserver.translation.*;
import de.zebrajaeger.phserver.util.SigmoidCalculator;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
@Slf4j
public class PanoHeadService {

    private static final int AXIS_INDEX_X = 0;
    private static final int AXIS_INDEX_Y = 2;
    private final HardwareService hardwareService;
    private final Axis x;
    private final Axis y;
    private final ApplicationEventPublisher applicationEventPublisher;

    private boolean joggingEnabled;
    private PanoHeadData panoHeadData;
    private final SigmoidCalculator sigmoid = new SigmoidCalculator();

    private final PreviousState previousState = new PreviousState();

    private long lastManualMove = 0;
    private boolean jogByJoystick = false;

    @Data
    static class PreviousState {

        private Camera camera;
        private boolean actorActive = false;
    }

    @Autowired
    public PanoHeadService(HardwareService hardwareService,
                           ApplicationEventPublisher applicationEventPublisher) {
        this.hardwareService = hardwareService;
        this.applicationEventPublisher = applicationEventPublisher;

        final AxisParameters axisParametersX = new AxisParameters(
                new DefaultStepperParameters(),
                MotorDriverParameters.MDP_16,
                new SpurGearParameters());
        x = new Axis(hardwareService.getPanoHead(), AXIS_INDEX_X, axisParametersX, true);

        final AxisParameters axisParametersY = new AxisParameters(
                new DefaultStepperParameters(350),
                MotorDriverParameters.MDP_16,
                new WormGearParameters());
        y = new Axis(hardwareService.getPanoHead(), AXIS_INDEX_Y, axisParametersY, false);
    }

    @Scheduled(initialDelay = 0, fixedRateString = "${controller.power.period:250}")
    public void updatePowerConsumption() {
        final Optional<PowerGauge> powerGauge = hardwareService.getPowerGauge();
        if (powerGauge.isPresent()) {
            try {
                double u = powerGauge.get().readVoltageInMillivolt() / 1000d;
                double i = powerGauge.get().readCurrentInMilliampere() / 1000d;
                Power power = new Power(u, i);
                applicationEventPublisher.publishEvent(new PowerMeasureEvent(power));
            } catch (IOException e) {
                log.debug("Could not read PowerGauge");
            }
        }
    }

//    @Scheduled(initialDelay = 0, fixedRateString = "${controller.acceleration.period:500}")
//    public void updateAccelerationSensor() throws IOException {
//        hardwareService.getAccelerationSensor().foo();
//    }

    public Position getCurrentPosition() {
        return new Position(x.getDegValue(), y.getDegValue());
    }

    public RawPosition getCurrentRawPosition() {
        return new RawPosition(x.getRawValue(), y.getRawValue());
    }

    @Scheduled(initialDelay = 0, fixedRateString = "${controller.main.period:100}")
    public void update() {

        try {
            panoHeadData = hardwareService.getPanoHead().read();
        } catch (IOException e) {
            log.debug("Could not read data from hardware device", e);
            return;
        }
        applicationEventPublisher.publishEvent(new PanoHeadDataEvent(panoHeadData));

        x.setRawValue(panoHeadData.getActor().getByIndex(AXIS_INDEX_X).getPos());
        y.setRawValue(panoHeadData.getActor().getByIndex(AXIS_INDEX_Y).getPos());
        applicationEventPublisher.publishEvent(
                new PositionEvent(getCurrentRawPosition(), getCurrentPosition()));

        // TODO set focus/trigger on start shot
        // TODO same with movement

//    boolean cameraActive = panoHeadData.getCamera().isActive();
        boolean actorActive = panoHeadData.getActor().isActive();

        Camera camera = panoHeadData.getCamera();
        if (!camera.equals(previousState.getCamera())) {
            if (previousState.getCamera() == null || (!camera.isTrigger() && previousState.getCamera()
                    .isTrigger())) {
                applicationEventPublisher.publishEvent(new ShotDoneEvent());
            }
            previousState.setCamera(new Camera(camera));
            applicationEventPublisher.publishEvent(new CameraChangedEvent(camera));
        }

        if (previousState.isActorActive() && !actorActive) {
//      log.info("MOVEMENT STOPPED");
            applicationEventPublisher.publishEvent(new MovementStoppedEvent());
        }

        if (previousState.isActorActive() != actorActive) {
            applicationEventPublisher.publishEvent(new ActorActiveChangedEvent(actorActive));
        }

        previousState.setActorActive(actorActive);
    }

    public PanoHeadData getData() {
        return panoHeadData;
    }

    public boolean isJoggingEnabled() {
        return joggingEnabled;
    }

    public void setToZero() {
        try {
            x.setToZero();
            y.setToZero();
        } catch (IOException e) {
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
        if (!isJoggingEnabled()) {
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
        if (!isJoggingEnabled()) {
            return;
        }

        try {
            x.moveRelative(relPosition.getX());
            y.moveRelative(relPosition.getY());
        } catch (IOException e) {
            log.debug("Could not move", e);
        }
    }

    public void manualAbsoluteMove(Position position) {
        if (!isJoggingEnabled()) {
            return;
        }
        try {
            x.moveTo(position.getX());
            y.moveTo(position.getY());
        } catch (IOException e) {
            log.debug("Could not move", e);
        }
    }

    /**
     * @param position to go to
     * @return true: already at the required position; false: move required
     */
    public boolean goTo(Position position) throws IOException {
        if (isJoggingEnabled()) {
            return false;
        }
        boolean _x = x.moveTo(position.getX());
        boolean _y = y.moveTo(position.getY());
        return _x && _y;
    }

    public void stopAll() {
        try {
            hardwareService.getPanoHead().stopAll();
        } catch (IOException e) {
            log.debug("Could stop all", e);
        }
    }

    public void shot(int focusTimeMs, int triggerTimeMs) throws IOException {
        hardwareService.getPanoHead().startShot(focusTimeMs, triggerTimeMs);
    }

    public void adaptAxisOffset() {
        try {
            x.adaptOffset();
            y.adaptOffset();
        } catch (IOException e) {
            log.debug("Could not adapt axis", e);
        }
    }

    public void normalizeAxisPosition() {
        x.normalizeAxisPosition();
        y.normalizeAxisPosition();
    }

    @EventListener
    public void onJoystickPosChanged(JoystickPositionEvent joystickPosition) {
        manualMoveByJoystick(joystickPosition.position());
    }

    @EventListener
    public void onJoystickConnectionChanged(JoystickConnectionEvent joystickConnectionEvent) {
        if (!joystickConnectionEvent.connected()) {
            stopAll();
        }
    }

    public void manualMoveByJoystick(Position joystickPosition) {
        if (!isJoggingEnabled()) {
            return;
        }

        setSigmoidSpeed(joystickPosition);
    }

    public void manualMoveByJoystickWithEmergencyStopOnTimeout(Position joystickPosition) {
        if (!isJoggingEnabled()) {
            return;
        }

        jogByJoystick = true;

        // reset watchdog timeout
        lastManualMove = System.currentTimeMillis();
        setSigmoidSpeed(joystickPosition);
    }

    public void manualMoveByJoystickStop() {
        if (!isJoggingEnabled()) {
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
        } catch (IOException e) {
            log.debug("Could not set velocity", e);
        }
    }

    public Position getCurrentPositionDeg() {
        return new Position(x.getDegValue(), y.getDegValue());
    }

}

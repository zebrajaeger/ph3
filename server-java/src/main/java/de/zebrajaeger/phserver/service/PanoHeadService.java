package de.zebrajaeger.phserver.service;

import de.zebrajaeger.phserver.data.Camera;
import de.zebrajaeger.phserver.data.PanoHeadData;
import de.zebrajaeger.phserver.data.Position;
import de.zebrajaeger.phserver.data.Power;
import de.zebrajaeger.phserver.data.RawPosition;
import de.zebrajaeger.phserver.event.CameraChangedEvent;
import de.zebrajaeger.phserver.event.JoggingChangedEvent;
import de.zebrajaeger.phserver.event.JoystickPositionEvent;
import de.zebrajaeger.phserver.event.MovementStoppedEvent;
import de.zebrajaeger.phserver.event.PanoHeadDataEvent;
import de.zebrajaeger.phserver.event.PositionEvent;
import de.zebrajaeger.phserver.event.PowerMeasureEvent;
import de.zebrajaeger.phserver.event.ShotDoneEvent;
import de.zebrajaeger.phserver.hardware.HardwareService;
import de.zebrajaeger.phserver.hardware.PowerGauge;
import de.zebrajaeger.phserver.util.SigmoidCalculator;
import java.io.IOException;
import java.util.Optional;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class PanoHeadService {

  private static final Logger LOG = LoggerFactory.getLogger(PanoHeadService.class);
  private static final int AXIS_INDEX_X = 0;
  private static final int AXIS_INDEX_Y = 2;
  private final HardwareService hardwareService;
  private final Axis x;
  private final Axis y;
  private final ApplicationEventPublisher applicationEventPublisher;
  @Value("${jogging.speed:20}")
  private float joggingSpeed;

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
      AxisTranslatorService axisTranslatorService,
      ApplicationEventPublisher applicationEventPublisher) {
    this.hardwareService = hardwareService;
    this.applicationEventPublisher = applicationEventPublisher;
    x = new Axis(hardwareService.getPanoHead(), AXIS_INDEX_X, axisTranslatorService, true);
    y = new Axis(hardwareService.getPanoHead(), AXIS_INDEX_Y, axisTranslatorService, false);
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
        LOG.debug("Could not read PowerGauge");
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
      LOG.debug("Could not read data from hardware device", e);
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
      applicationEventPublisher.publishEvent(new MovementStoppedEvent());
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
      LOG.debug("Could not set zu zero", e);
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
      try {
        stopAll();
      } catch (IOException e) {
        LOG.debug("Could not stop all", e);
      }
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
      LOG.debug("Could not move", e);
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
      LOG.debug("Could not move", e);
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

  public void stopAll() throws IOException {
    hardwareService.getPanoHead().stopAll();
  }

  public void shot(int focusTimeMs, int triggerTimeMs) throws IOException {
    hardwareService.getPanoHead().startShot(focusTimeMs, triggerTimeMs);
  }

  public void adaptAxisOffset() {
    try {
      x.adaptOffset();
      y.adaptOffset();
    } catch (IOException e) {
      LOG.debug("Could not adapt axis", e);
    }
  }

  public void normalizeAxisPosition() {
    x.normalizeAxisPosition();
    y.normalizeAxisPosition();
  }

  @EventListener
  public void onJoystickPosChanged(JoystickPositionEvent joystickPosition) {
    manualMoveByJoystick(joystickPosition.getPosition());
  }

  public void manualMoveByJoystick(Position joystickPosition) {
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

    try {
      stopAll();
    } catch (IOException e) {
      LOG.debug("Could not stop all", e);
    }
  }

  @Scheduled(fixedDelay = 100)
  public void checkJoystickMovementTimeout() {
    long now = System.currentTimeMillis();

    // emergency stop ?
    if (jogByJoystick && now - lastManualMove > 200) {
      jogByJoystick = false;
      LOG.warn("Emergency stop. Reason: Joystick event timeout");
      try {
        stopAll();
      } catch (IOException e) {
        LOG.debug("Could not stop movement", e);
      }
    }
  }

  private void setSigmoidSpeed(Position speed) {
    Position speed1 = speed.withBorderOfOne();
    int xSpeed = (int) (sigmoid.value(speed1.getX()) * joggingSpeed);
    int ySpeed = (int) (sigmoid.value(speed1.getY()) * joggingSpeed);
    try {
      x.setVelocityDeg(xSpeed);
      y.setVelocityDeg(ySpeed);
    } catch (IOException e) {
      LOG.debug("Could not set velocity", e);
    }
  }
}

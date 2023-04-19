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
  private final HardwareService hardwareService;
  private final Axis x;
  private final Axis y;
  private final ApplicationEventPublisher applicationEventPublisher;
  @Value("${jogging.speed:1000}")
  private float joggingSpeed;

  private boolean joggingEnabled;
  private PanoHeadData panoHeadData;
  private final SigmoidCalculator sigmoid = new SigmoidCalculator();

  private final PreviousState previousState = new PreviousState();
//  private Position currentPosition;

  private long lastManualMove = 0;
  private boolean jogByJoystick = false;

  static class PreviousState {

    private Camera camera;
    private boolean actorActive = false;

    public Camera getCamera() {
      return camera;
    }

    public void setCamera(Camera camera) {
      this.camera = camera;
    }

    public boolean isActorActive() {
      return actorActive;
    }

    public void setActorActive(boolean actorActive) {
      this.actorActive = actorActive;
    }
  }

  @Autowired
  public PanoHeadService(HardwareService hardwareService,
      AxisTranslatorService axisTranslatorService,
      ApplicationEventPublisher applicationEventPublisher) {
    this.hardwareService = hardwareService;
    this.applicationEventPublisher = applicationEventPublisher;
    x = new Axis(hardwareService.getPanoHead(), 0, axisTranslatorService, true);
    y = new Axis(hardwareService.getPanoHead(), 1, axisTranslatorService, false);
  }

  @Scheduled(initialDelay = 0, fixedRateString = "${controller.power.period:250}")
  public void updatePowerConsumption() throws IOException {
    final Optional<PowerGauge> powerGauge = hardwareService.getPowerGauge();
    if (powerGauge.isPresent()) {
      double u = powerGauge.get().readVoltageInMillivolt() / 1000d;
      double i = powerGauge.get().readCurrentInMilliampere() / 1000d;
      Power power = new Power(u, i);
      applicationEventPublisher.publishEvent(new PowerMeasureEvent(power));
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
  public void update() throws IOException {

    panoHeadData = hardwareService.getPanoHead().read();
    applicationEventPublisher.publishEvent(new PanoHeadDataEvent(panoHeadData));

    x.setRawValue(panoHeadData.getActor().getX().getPos());
    y.setRawValue(panoHeadData.getActor().getY().getPos());
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

  public void setToZero() throws IOException {
    hardwareService.getPanoHead().resetPos();
  }

  /**
   * Also publishes JoggingChangedEvent
   */
  public void setJoggingEnabled(boolean joggingEnabled) throws IOException {
    if (joggingEnabled == this.joggingEnabled) {
      return;
    }

    this.joggingEnabled = joggingEnabled;

    // if jogging is disabled now, we stop the movement
    if (!isJoggingEnabled()) {
      stopMovement();
    }

    applicationEventPublisher.publishEvent(new JoggingChangedEvent(joggingEnabled));
  }

  /**
   * Move the head for the given degrees, based on current Position
   *
   * @param relPosition way to move in deg
   */
  public void manualRelativeMove(Position relPosition) throws IOException {
    if (!isJoggingEnabled()) {
      return;
    }

    x.moveRelative(relPosition.getX());
    y.moveRelative(relPosition.getY());
  }

  public void manualAbsoluteMove(Position position) throws IOException {
    if (!isJoggingEnabled()) {
      return;
    }
    x.moveTo(position.getX());
    y.moveTo(position.getY());
  }

  /**
   * @param position to go to
   * @return true: already at the required position; false: move required
   */
  public boolean goTo(Position position) throws IOException {
    if (isJoggingEnabled()) {
      return false;
    }
    return x.moveTo(position.getX()) && y.moveTo(position.getY());
  }

  public void stopAll() throws IOException {
    hardwareService.getPanoHead().setTargetVelocity(0, 0);
    hardwareService.getPanoHead().setTargetVelocity(1, 0);
  }

  public void shot(int focusTimeMs, int triggerTimeMs) throws IOException {
    hardwareService.getPanoHead().startShot(focusTimeMs, triggerTimeMs);
  }

  public void adaptAxisOffset() throws IOException {
    x.adaptOffset();
    y.adaptOffset();
  }

  public void normalizeAxisPosition() throws IOException {
    x.normalizeAxisPosition();
    y.normalizeAxisPosition();
  }

  @EventListener
  public void onJoystickPosChanged(JoystickPositionEvent joystickPosition) throws IOException {
    manualMoveByJoystick(joystickPosition.getPosition());
  }

  public void manualMoveByJoystick(Position joystickPosition) throws IOException {
    if (!isJoggingEnabled()) {
      return;
    }

    jogByJoystick = true;

    // reset watchdog timeout
    lastManualMove = System.currentTimeMillis();
    setSigmoidSpeed(joystickPosition);
  }

  public void manualMoveByJoystickStop() throws IOException {
    if (!isJoggingEnabled()) {
      return;
    }

    jogByJoystick = false;
    stopMovement();
  }

  @Scheduled(fixedDelay = 100)
  public void checkJoystickMovementTimeout() throws IOException {
    long now = System.currentTimeMillis();

    // emergency stop ?
    if (jogByJoystick && now - lastManualMove > 200) {
      jogByJoystick = false;
      LOG.warn("Emergency stop. Reason: Joystick event timeout");
      stopMovement();
    }
  }

  private void setSigmoidSpeed(Position speed) throws IOException {
    Position speed1 = speed.withBorderOfOne();
    int x = (int) (sigmoid.value(speed1.getX()) * joggingSpeed);
    int y = (int) (sigmoid.value(speed1.getY()) * joggingSpeed);
    hardwareService.getPanoHead().setTargetVelocity(0, x);
    hardwareService.getPanoHead().setTargetVelocity(1, y);
  }

  private void stopMovement() throws IOException {
    hardwareService.getPanoHead().setTargetVelocity(0, 0);
    hardwareService.getPanoHead().setTargetVelocity(1, 0);
  }
}

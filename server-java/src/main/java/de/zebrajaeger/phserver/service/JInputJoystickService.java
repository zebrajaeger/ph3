package de.zebrajaeger.phserver.service;

import de.zebrajaeger.phserver.data.Position;
import de.zebrajaeger.phserver.event.JoystickPositionEvent;
import de.zebrajaeger.phserver.util.MathUtils;
import java.util.Arrays;
import java.util.Optional;
import net.java.games.input.Component.Identifier.Axis;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class JInputJoystickService implements JoystickService {

  private final static Logger LOG = LoggerFactory.getLogger(JInputJoystickService.class);

  private final static Position ZERO_POSITION = new Position(0, 0);

  @Value("${joystick.rescan.period:2000}")
  private long rescanPeriod;
  @Value("${joystick.border:0.05}")
  private float joystickBorder;
  private final ApplicationEventPublisher applicationEventPublisher;

  private Controller controller;
  private long nextRescan = 0;
  private Position position;

  public JInputJoystickService(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  @Override
  public Position getPosition() {
    return position;
  }

  @Scheduled(initialDelay = 0, fixedRateString = "${joystick.usb.period:50}")
  private void update() {
    try {
      if (readNewPosition()) {
        LOG.debug("Position update to {}", position);
        applicationEventPublisher.publishEvent(new JoystickPositionEvent(position));
      }
    } catch (IllegalStateException e) {
      LOG.info("Cannot read Joystick Position", e);
    }
  }

  private float mapToBorder(float value) {
    if (value > joystickBorder) {
      return MathUtils.map(value, joystickBorder, 1f, 0f, 1f);
    } else if (value < -joystickBorder) {
      return MathUtils.map(value, -joystickBorder, -1f, 0f, -1f);
    }

    return 0;
  }

  private boolean readNewPosition() {
    // check for controller and rescan if needed
    if (controller == null) {
      LOG.debug("Scan for a new  controller");
      long now = System.currentTimeMillis();
      if (nextRescan < now) {
        nextRescan = now + rescanPeriod;
        Optional<Controller> c = scanForGameController();
        c.ifPresent(value -> {
          controller = value;
          LOG.info("Found a Controller: {}", controller);
        });
      }
    }

    // no controller no fun
    if (controller == null) {
      return false;
    }

    if (controller.poll()) {
      // controller valid -> get x and y pos
      final EventQueue q = controller.getEventQueue();
      Event e = new Event();
      float x = 0;
      float y = 0;
      boolean hasX = false;
      boolean hasY = false;
      while (q.getNextEvent(e)) {
        if (Axis.X == e.getComponent().getIdentifier()) {
          x = e.getValue();
          hasX = true;
        } else if (Axis.Y == e.getComponent().getIdentifier()) {
          y = e.getValue();
          hasY = true;
        }
      }

      if (hasX && hasY) {
        // new x and y
        position = new Position(mapToBorder(x), mapToBorder(y));
        return true;
      } else if (!hasX && !hasY) {
        // no x and no y
        return false;
      } else {
        if (hasX) {
          // new x only
          if (position != null) {
            position = new Position(mapToBorder(x), position.getY());
          } else {
            position = new Position(mapToBorder(x), 0);
          }
        } else {
          // new y only
          if (position != null) {
            position = new Position(position.getX(), mapToBorder(y));
          } else {
            position = new Position(0, mapToBorder(y));
          }
        }
        return true;
      }
    } else {
      // controller invalid
      controller = null;
      LOG.info("Controller lost");
      if (position == ZERO_POSITION) {
        return false;
      } else {
        position = ZERO_POSITION;
        return true;
      }
    }
  }

  /**
   * Find first game controller
   */
  private Optional<Controller> scanForGameController() {
    return readControllerEnvironment()
        .flatMap(controllerEnvironment -> Arrays.stream(controllerEnvironment.getControllers())
            .filter(
                c -> c.getType() == Controller.Type.GAMEPAD || c.getType() == Controller.Type.STICK)
            .findFirst());
  }

  /**
   * code partial from <a
   * href="https://github.com/sgothel/jinput/blob/6e0238be1829a0663b2bf70fe79faf32f589ab2a/coreAPI/src/java/net/java/games/input/DefaultControllerEnvironment.java#L136">here</a>
   */
  private Optional<ControllerEnvironment> readControllerEnvironment() {
    String osName = System.getProperty("os.name", "").trim();
    ControllerEnvironment ce = null;
    if (osName.equals("Linux")) {
      ce = new net.java.games.input.LinuxEnvironmentPlugin();
    } else if (osName.equals("Mac OS X")) {
      ce = new net.java.games.input.OSXEnvironmentPlugin();
    } else if (osName.toLowerCase().contains("windows")) {
      ce = new net.java.games.input.DirectAndRawInputEnvironmentPlugin();
    }
    return Optional.ofNullable(ce);
  }
}

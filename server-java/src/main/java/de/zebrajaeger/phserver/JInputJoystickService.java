package de.zebrajaeger.phserver;

import de.zebrajaeger.phserver.data.Position;
import de.zebrajaeger.phserver.event.JoystickPositionEvent;
import net.java.games.input.Component.Identifier.Axis;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class JInputJoystickService implements JoystickService {
    private final static Logger LOG = LoggerFactory.getLogger(JInputJoystickService.class);

    private final static long RESCAN_PERIOD = 2000;
    private final static Position ZERO_POSITION = new Position(0, 0);

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
        if (readNewPosition()) {
            LOG.debug("Position update to {}", position);
            applicationEventPublisher.publishEvent(new JoystickPositionEvent(position));
        }
    }

    private boolean readNewPosition() {
        // check for controller and rescan if needed
        if (controller == null) {
            long now = System.currentTimeMillis();
            if (nextRescan < now) {
                // TODO log
                LOG.info("Rescan Controller");
                nextRescan = now + RESCAN_PERIOD;
                Optional<Controller> c = scanForGameController();
                c.ifPresent(value -> controller = value);
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
                position = new Position(x, y);
                return true;
            } else if (!hasX && !hasY) {
                return false;
            } else {
                if (hasX) {
                    if (position != null) {
                        position = new Position(x, position.getY());
                    } else {
                        position = new Position(x, 0);
                    }
                } else {
                    if (position != null) {
                        position = new Position(position.getX(), y);
                    } else {
                        position = new Position(0, y);
                    }
                }
                return true;
            }
        } else {
            // controller invalid
            controller = null;
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
        return readControllerEnvironment().flatMap(controllerEnvironment ->
                Arrays.stream(controllerEnvironment.getControllers())
                        .filter(c -> c.getType() == Controller.Type.GAMEPAD || c.getType() == Controller.Type.STICK)
                        .findFirst());
    }

    /**
     * code partial from https://github.com/sgothel/jinput/blob/6e0238be1829a0663b2bf70fe79faf32f589ab2a/coreAPI/src/java/net/java/games/input/DefaultControllerEnvironment.java#L136
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

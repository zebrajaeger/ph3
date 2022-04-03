package de.zebrajaeger.phserver;

import de.zebrajaeger.phserver.data.Position;
import de.zebrajaeger.phserver.event.JoystickPositionEvent;
import de.zebrajaeger.phserver.hardware.HardwareService;
import org.lwjgl.glfw.GLFW;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.FloatBuffer;

@Service
public class UsbJoystickService implements JoystickService {

    private final HardwareService hardwareService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private Position position = new Position(0, 0);

    @PostConstruct
    public void init() {
        // no crash is already a good start...
        GLFW.glfwInit();
    }

    @PreDestroy
    public void shutdown() {
        GLFW.glfwTerminate();
    }

    @Autowired
    public UsbJoystickService(HardwareService hardwareService, ApplicationEventPublisher applicationEventPublisher) {
        this.hardwareService = hardwareService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Scheduled(initialDelay = 0, fixedRateString = "${joystick.usb.period:50}")
    public void update() {
        FloatBuffer fb = GLFW.glfwGetJoystickAxes(GLFW.GLFW_JOYSTICK_1);
        if (fb != null && fb.capacity() >= 2) {
            double x = fb.get(0);
            double y = fb.get(1);
            if (x > -0.05 && x < 0.05d) {
                x = 0;
            }
            if (y > -0.05 && y < 0.05d) {
                y = 0;
            }
            position = new Position(x, y);
            applicationEventPublisher.publishEvent(new JoystickPositionEvent(position));
        }
    }

    @Override
    public Position getPosition() {
        return position;
    }
}

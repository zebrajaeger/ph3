package de.zebrajaeger.phserver.hardware.fake;

import de.zebrajaeger.phserver.event.CameraChangedEvent;
import de.zebrajaeger.phserver.event.ShotDoneEvent;
import de.zebrajaeger.phserver.hardware.HardwareService;
import de.zebrajaeger.phserver.hardware.Joystick;
import de.zebrajaeger.phserver.hardware.PanoHead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
@Profile({"develop"})
public class FakeService implements HardwareService {
    private static final Logger LOG = LoggerFactory.getLogger(FakeService.class);

    private FakeJoystick joystick;
    private FakePanoHead panoHead;

    @Value("${develop.joystick.index:0}")
    private int joystickIndex;
    @Value("${develop.joystick.axis.index.x:0}")
    private int joystickAxisIndexX;
    @Value("${develop.joystick.axis.index.y:1}")
    private int joystickAxisIndexY;
    @Value("${develop.updatesPerSecond:5}")
    private int updatesPerSecond;

    @Override
    public Joystick getJoystick() {
        return joystick;
    }

    @Override
    public PanoHead getPanoHead() {
        return panoHead;
    }

    @Scheduled(fixedRateString = "${develop.updatesPerSecond:5}")
    public void update() {
        panoHead.update();
    }

    public void reset() {
        panoHead.reset();
    }

    @PostConstruct
    public void init() {
        joystick = new FakeJoystick(joystickIndex, joystickAxisIndexX, joystickAxisIndexY);
        joystick.init();
        panoHead = new FakePanoHead(1000 / updatesPerSecond);
    }

    @PreDestroy
    public void destroy() {
        joystick.destroy();
    }

    @EventListener
    public void printCameraEvent(CameraChangedEvent cameraChangedEvent) {
        LOG.info("Camera changed: '{}'", cameraChangedEvent);
    }

    @EventListener
    public void printShotDone(ShotDoneEvent shotDoneEvent) {
        LOG.info("Camera shot done: '{}'", shotDoneEvent);
    }
}

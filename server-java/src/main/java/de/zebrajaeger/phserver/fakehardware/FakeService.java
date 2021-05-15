package de.zebrajaeger.phserver.fakehardware;

import de.zebrajaeger.phserver.HardwareService;
import de.zebrajaeger.phserver.Joystick;
import de.zebrajaeger.phserver.PanoHead;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Profile({"develop"})
public class FakeService implements HardwareService {

    private final FakeJoystick joystick;
    private final FakePanoHead panoHead;

    @Autowired
    public FakeService() {
        joystick = new FakeJoystick();
        panoHead = new FakePanoHead();
    }

    @Override
    public Joystick getJoystick() {
        return joystick;
    }

    @Override
    public PanoHead getPanoHead() {
        return panoHead;
    }

    @Scheduled(fixedRate = 5)
    public void update() {
        panoHead.update();
    }

    public void reset() {
        joystick.reset();
        panoHead.reset();
    }
}

package de.zebrajaeger.phserver.hardware.fake;

import de.zebrajaeger.phserver.event.CameraChangedEvent;
import de.zebrajaeger.phserver.event.ShotDoneEvent;
import de.zebrajaeger.phserver.hardware.AccelerationSensor;
import de.zebrajaeger.phserver.hardware.HardwareService;
import de.zebrajaeger.phserver.hardware.PanoHead;
import de.zebrajaeger.phserver.hardware.PowerGauge;
import de.zebrajaeger.phserver.hardware.SystemDevice;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Profile({"develop"})
public class FakeService implements HardwareService {
    private static final Logger LOG = LoggerFactory.getLogger(FakeService.class);

    private FakePanoHead panoHead;

    @Value("${develop.updatesPerSecond:5}")
    private int updatesPerSecond;

    @Override
    public Optional<PanoHead> getPanoHead() {
        return Optional.of(panoHead);
    }

    @Override
    public Optional<PowerGauge> getPowerGauge() {
        return Optional.empty();
    }

    @Override
    public Optional<AccelerationSensor> getAccelerationSensor() {
        return Optional.empty();
    }

    @Override
    public Optional<SystemDevice> getSystemDevice() {
        return Optional.empty();
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
        panoHead = new FakePanoHead(1000 / updatesPerSecond);
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

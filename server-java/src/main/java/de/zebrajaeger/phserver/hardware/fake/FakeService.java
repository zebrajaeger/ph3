package de.zebrajaeger.phserver.hardware.fake;

import de.zebrajaeger.phserver.event.CameraChangedEvent;
import de.zebrajaeger.phserver.event.ShotDoneEvent;
import de.zebrajaeger.phserver.hardware.*;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Deprecated
@Service
@Profile({"develop"})
public class FakeService implements HardwareService {

//    private static final Logger LOG = LoggerFactory.getLogger(FakeService.class);
//
//    private FakePanoHead panoHead;
//    private final FakeGpsReceiver gpsDevice = new FakeGpsReceiver();
//    private final FakeSystemControl systemDevice = new FakeSystemControl();
//
//    private final FakePowerGauge powerGauge = new FakePowerGauge();
//
//    @Value("${develop.updatesPerSecond:5}")
//    private int updatesPerSecond;
//
//    @Override
//    public PollingPanoHead getPanoHead() {
//        return panoHead;
//    }
//
//    @Override
//    public PowerGauge getPowerGauge() {
//        return powerGauge;
//    }
//
//    @Override
//    public Optional<AccelerationSensor> getAccelerationSensor() {
//        return Optional.empty();
//    }
//
//    @Override
//    public SystemControl getSystemDevice() {
//        return systemDevice;
//    }
//
//    @Override
//    public GpsDevice getGpsDevice() {
//        return gpsDevice;
//    }
//
//    @Scheduled(fixedRateString = "${develop.updatesPerSecond:5}")
//    public void update() {
//        panoHead.update();
//    }
//
//    public void reset() {
//        panoHead.reset();
//    }
//
//    @PostConstruct
//    public void init() {
//        panoHead = new FakePanoHead(1000 / updatesPerSecond);
//    }
//
//    @EventListener
//    public void printCameraEvent(CameraChangedEvent cameraChangedEvent) {
//        LOG.info("Camera changed: '{}'", cameraChangedEvent);
//    }
//
//    @EventListener
//    public void printShotDone(ShotDoneEvent shotDoneEvent) {
//        LOG.info("Camera shot done: '{}'", shotDoneEvent);
//    }
}

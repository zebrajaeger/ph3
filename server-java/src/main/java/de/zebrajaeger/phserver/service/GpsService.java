package de.zebrajaeger.phserver.service;

import de.zebrajaeger.phserver.data.GpsData;
import de.zebrajaeger.phserver.data.GpsLocation;
import de.zebrajaeger.phserver.event.GpsDataEvent;
import de.zebrajaeger.phserver.hardware.HardwareService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@Getter
public class GpsService {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final HardwareService hardwareService;
    private GpsData gpsData;

    public GpsService(ApplicationEventPublisher applicationEventPublisher, HardwareService hardwareService) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.hardwareService = hardwareService;
    }

    @Scheduled(initialDelay = 0, fixedRateString = "${controller.power.period:1000}")
    public void updateGps() {
        try {
            gpsData = hardwareService.getGpsDevice().read();
            log.info("Gps: {}", gpsData);
            applicationEventPublisher.publishEvent(new GpsDataEvent(gpsData));
        } catch (IOException e) {
            gpsData = null;
            log.debug("Could not read from GPS device");
        }
    }

    public GpsLocation getLocation(){
        if(gpsData==null){
            return null;
        }
        return gpsData.geoLocation();
    }
}

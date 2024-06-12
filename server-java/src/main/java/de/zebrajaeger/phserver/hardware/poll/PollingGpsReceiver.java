package de.zebrajaeger.phserver.hardware.poll;

import de.zebrajaeger.phserver.data.GpsData;
import de.zebrajaeger.phserver.event.GpsDataEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;

@Slf4j
public abstract class PollingGpsReceiver {
    private final ApplicationEventPublisher applicationEventPublisher;

    public PollingGpsReceiver(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Scheduled(initialDelay = 1000, fixedRateString = "${controller.power.period:10000}")
    public void updatePowerConsumption() {
        try {
            applicationEventPublisher.publishEvent(new GpsDataEvent(read()));
        } catch (IOException e) {
            log.debug("Could not read PowerGauge");
        }
    }

    public abstract GpsData read() throws IOException;
}

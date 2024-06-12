package de.zebrajaeger.phserver.hardware.poll;

import de.zebrajaeger.phserver.data.Power;
import de.zebrajaeger.phserver.event.PowerMeasureEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;

@Slf4j
public abstract class PollingPowerGauge {

    private final ApplicationEventPublisher applicationEventPublisher;

    public PollingPowerGauge(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Scheduled(initialDelay = 1000, fixedRateString = "${controller.power.period:250}")
    public void updatePowerConsumption() {
        try {
            double u = readVoltageInMillivolt() / 1000d;
            double i = readCurrentInMilliampere() / 1000d;
            applicationEventPublisher.publishEvent(new PowerMeasureEvent(new Power(u, i)));
        } catch (IOException e) {
            log.debug("Could not read PowerGauge");
        }
    }

    public abstract int readVoltageInMillivolt() throws IOException ;
    public abstract int readCurrentInMilliampere() throws IOException;
}

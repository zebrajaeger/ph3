package de.zebrajaeger.phserver.hardware.powergauge;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
//@Profile({"develop"})
@ConditionalOnProperty(name="enable.powergauge.fake")
public class FakePowerGauge extends PollingPowerGauge {
    public FakePowerGauge(ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
    }

    @Override
    public int readVoltageInMillivolt() {
        return (int) (15000 + (Math.random() * 6000));
    }

    @Override
    public int readCurrentInMilliampere() {
        return (int) (400 + (Math.random() * 300));
    }
}

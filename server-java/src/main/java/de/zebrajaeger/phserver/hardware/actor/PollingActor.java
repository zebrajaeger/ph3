package de.zebrajaeger.phserver.hardware.actor;

import de.zebrajaeger.phserver.data.ActorStatus;
import de.zebrajaeger.phserver.event.ActorStatusEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
public abstract class PollingActor implements ReadableActor{
    private final ApplicationEventPublisher applicationEventPublisher;

    public PollingActor(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Scheduled(initialDelay = 0, fixedRateString = "${controller.main.period:100}")
    public void onUpdate() {
        ActorStatus actorStatus;
        try {
            update();
            actorStatus = readActorStatus();
        } catch (Exception e) {
            log.debug("Could not read data from hardware device", e);
            return;
        }
        applicationEventPublisher.publishEvent(new ActorStatusEvent(actorStatus));
    }
}

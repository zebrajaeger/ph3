package de.zebrajaeger.phserver.hardware.poll;

import de.zebrajaeger.phserver.data.Position;
import de.zebrajaeger.phserver.event.JoystickPositionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
public abstract class PollingJoystick {
  private final ApplicationEventPublisher applicationEventPublisher;

  public PollingJoystick(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  @Scheduled(initialDelay = 0, fixedRateString = "${joystick.usb.period:50}")
  private void update() {
    try {
      Position position = getPosition();
      if (position!=null) {
        log.debug("Position update to {}", position);
        applicationEventPublisher.publishEvent(new JoystickPositionEvent(position));
      }
    } catch (IllegalStateException e) {
      log.info("Cannot read Joystick Position", e);
    }
  }

    public abstract Position getPosition();
}

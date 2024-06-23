package de.zebrajaeger.phserver.hardware.systemcontrol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"develop", "remotei2c"})
public class FakeSystemControl implements SystemControl {

  private static final Logger LOG = LoggerFactory.getLogger(FakeSystemControl.class);

  @Override
  public void shutdown() {
    LOG.warn("SHUTDOWN triggered (but this is a fake device, so nothing will happen");
  }

  @Override
  public void reboot() {
    LOG.warn("REBOOT triggered (but this is a fake device, so nothing will happen");
  }
  @Override
  public void restartApp() {
    LOG.warn("Restart App triggered (but this is a fake device, so nothing will happen");
  }
}

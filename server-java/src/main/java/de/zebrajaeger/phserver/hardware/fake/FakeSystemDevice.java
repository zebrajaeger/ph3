package de.zebrajaeger.phserver.hardware.fake;

import de.zebrajaeger.phserver.hardware.SystemDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakeSystemDevice implements SystemDevice {

  private static final Logger LOG = LoggerFactory.getLogger(FakeSystemDevice.class);

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

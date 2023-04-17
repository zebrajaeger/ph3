package de.zebrajaeger.phserver.hardware.fake;

import de.zebrajaeger.phserver.hardware.SystemDevice;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakeSystemDevice implements SystemDevice {

  private static final Logger LOG = LoggerFactory.getLogger(FakeSystemDevice.class);

  @Override
  public void shutdown(){
    LOG.warn("SHUTDOWN triggered (but this is a fake device, so nothing will happen");
  }
}

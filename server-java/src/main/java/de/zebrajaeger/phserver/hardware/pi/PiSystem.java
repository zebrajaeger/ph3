package de.zebrajaeger.phserver.hardware.pi;

import de.zebrajaeger.phserver.hardware.SystemDevice;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PiSystem implements SystemDevice {

  private static final Logger LOG = LoggerFactory.getLogger(PiSystem.class);

  @Override
  public void shutdown() throws IOException {
    LOG.info("Shutdown System");

    Runtime runtime = Runtime.getRuntime();
    Process p = runtime.exec("sudo shutdown -h now");
    final BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
    String l;
    while ((l = r.readLine()) != null) {
      LOG.info(l);
    }
  }
}

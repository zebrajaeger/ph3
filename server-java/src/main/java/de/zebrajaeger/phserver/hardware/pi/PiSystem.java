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
    executeCommand("sudo shutdown -h now");
  }

  @Override
  public void reboot() throws IOException {
    LOG.info("Reboot System");
    executeCommand("sudo reboot");
  }

  private void executeCommand(String command) throws IOException {
    Runtime runtime = Runtime.getRuntime();
    Process p = runtime.exec(command);
    final BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
    String l;
    while ((l = r.readLine()) != null) {
      LOG.info(l);
    }
  }

}

package de.zebrajaeger.phserver.hardware.local;

import de.zebrajaeger.phserver.hardware.SystemControl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"locali2c", "mqtt"})
public class LinuxSystemControl implements SystemControl {

  private static final Logger LOG = LoggerFactory.getLogger(LinuxSystemControl.class);

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

  @Override
  public void restartApp() throws IOException {
    LOG.info("Restart App");
    executeCommand("sudo service browser restart");
    executeCommand("sudo service ph restart");
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

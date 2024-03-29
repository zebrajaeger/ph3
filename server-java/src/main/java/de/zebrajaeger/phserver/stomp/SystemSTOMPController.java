package de.zebrajaeger.phserver.stomp;

import de.zebrajaeger.phserver.hardware.HardwareService;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class SystemSTOMPController {

  private final static Logger LOG = LoggerFactory.getLogger(SystemSTOMPController.class);

  private final HardwareService hardwareService;

  public SystemSTOMPController(HardwareService hardwareService) {
    this.hardwareService = hardwareService;
  }

  @MessageMapping("/system/shutdown")
  public void shutdown() throws IOException {
    LOG.info("Shutdown System (STOMP)");
    hardwareService.getSystemDevice().shutdown();
  }

  @MessageMapping("/system/reboot")
  public void reboot() throws IOException {
    LOG.info("Reboot System (STOMP)");
    hardwareService.getSystemDevice().reboot();
  }

  @MessageMapping("/system/restartApp")
  public void restartApp() throws IOException {
    LOG.info("Restart App (STOMP)");
    hardwareService.getSystemDevice().restartApp();
  }
}

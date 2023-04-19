package de.zebrajaeger.phserver.rest;

import de.zebrajaeger.phserver.hardware.HardwareService;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class SystemController {

  private final static Logger LOG = LoggerFactory.getLogger(SystemController.class);

  private final HardwareService hardwareService;

  public SystemController(HardwareService hardwareService) {
    this.hardwareService = hardwareService;
  }

  @GetMapping("system/shutdown")
  public void shutdown() throws IOException {
    LOG.info("Shutdown System (REST)");
    hardwareService.getSystemDevice().shutdown();
  }

  @GetMapping("system/reboot")
  public void reboot() throws IOException {
    LOG.info("Reboot System (REST)");
    hardwareService.getSystemDevice().reboot();
  }
}

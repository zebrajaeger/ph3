package de.zebrajaeger.phserver.stomp;

import de.zebrajaeger.phserver.hardware.HardwareService;
import de.zebrajaeger.phserver.hardware.SystemDevice;
import java.io.IOException;
import java.util.Optional;
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
  public void start() throws IOException {
    LOG.info("Shutdown System (STOMP)");
    final Optional<SystemDevice> systemDevice = hardwareService.getSystemDevice();
    if (systemDevice.isPresent()) {
      systemDevice.get().shutdown();
    }
  }

}

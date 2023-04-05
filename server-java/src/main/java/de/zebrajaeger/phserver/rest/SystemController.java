package de.zebrajaeger.phserver.rest;

import de.zebrajaeger.phserver.hardware.HardwareService;
import de.zebrajaeger.phserver.hardware.SystemDevice;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import org.apache.commons.lang3.ArrayUtils;
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

  @GetMapping("shutdown")
  public void shutdown() throws IOException {
    LOG.info("Shutdown System (REST)");
    final Optional<SystemDevice> systemDevice = hardwareService.getSystemDevice();
    if(systemDevice.isPresent()){
      systemDevice.get().shutdown();
    }
  }
}

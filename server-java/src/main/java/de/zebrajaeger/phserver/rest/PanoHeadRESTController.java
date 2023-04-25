package de.zebrajaeger.phserver.rest;

import de.zebrajaeger.phserver.service.PanoHeadService;
import de.zebrajaeger.phserver.data.Actor;
import de.zebrajaeger.phserver.data.PanoHeadData;
import de.zebrajaeger.phserver.hardware.HardwareService;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
@Slf4j
public class PanoHeadRESTController {

  private final PanoHeadService panoHeadService;
  private final HardwareService hardwareService;

  @Autowired
  public PanoHeadRESTController(PanoHeadService panoHeadService, HardwareService hardwareService) {
    this.panoHeadService = panoHeadService;
    this.hardwareService = hardwareService;
  }

  @GetMapping("device")
  public PanoHeadData data() {
    return panoHeadService.getData();
  }

  @GetMapping("jogging")
  public boolean getJogging() {
    return panoHeadService.isJoggingEnabled();
  }

  @PutMapping("jogging")
  public boolean setJogging(@RequestParam boolean jogging){
    panoHeadService.setJoggingEnabled(jogging);
    return panoHeadService.isJoggingEnabled();
  }

  @GetMapping("actor")
  public Actor actor() {
    return panoHeadService.getData().getActor();
  }

  @PutMapping("actor/limit")
  public void limit(@RequestParam int axisIndex, @RequestParam int limit){
    // TODO no hardware access on this layer
    try {
      hardwareService.getPanoHead().setLimit(axisIndex, limit);
    } catch (IOException e) {
      log.debug("Could not set limit", e);
    }
  }

  @PutMapping("actor/pos/{axisIndex}")
  public void pos(@PathVariable int axisIndex, @RequestParam int pos) {
    // TODO no hardware access on this layer
    try {
      hardwareService.getPanoHead().setTargetPos(axisIndex, pos);
    } catch (IOException e) {
      log.debug("Could not set target pos", e);
    }
  }

  @PutMapping("actor/velocity/{axisIndex}")
  public void velocity(@PathVariable int axisIndex, @RequestParam int velocity)  {
    // TODO no hardware access on this layer
    try {
      hardwareService.getPanoHead().setTargetVelocity(axisIndex, velocity);
    } catch (IOException e) {
      log.debug("Could not set target velocity", e);
    }
  }
}

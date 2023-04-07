package de.zebrajaeger.phserver.rest;

import de.zebrajaeger.phserver.PanoHeadService;
import de.zebrajaeger.phserver.data.Actor;
import de.zebrajaeger.phserver.data.PanoHeadData;
import de.zebrajaeger.phserver.hardware.HardwareService;
import de.zebrajaeger.phserver.hardware.PanoHead;
import java.io.IOException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
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
  public boolean setJogging(@RequestParam boolean jogging) throws IOException {
    panoHeadService.setJoggingEnabled(jogging);
    return panoHeadService.isJoggingEnabled();
  }

  @GetMapping("actor")
  public Actor actor() {
    return panoHeadService.getData().getActor();
  }

  @PutMapping("actor/limit")
  public void limit(@RequestParam int axisIndex, @RequestParam int limit) throws IOException {

    final Optional<PanoHead> panoHead = hardwareService.getPanoHead();
    if (panoHead.isPresent()) {
      panoHead.get().setLimit(axisIndex, limit);
    }
  }

  @PutMapping("actor/pos/{axisIndex}")
  public void pos(@PathVariable int axisIndex, @RequestParam int pos) throws IOException {
    final Optional<PanoHead> panoHead = hardwareService.getPanoHead();
    if (panoHead.isPresent()) {
      panoHead.get().setTargetPos(axisIndex, pos);
    }
  }

  @PutMapping("actor/velocity/{axisIndex}")
  public void velocity(@PathVariable int axisIndex, @RequestParam int velocity) throws IOException {
    final Optional<PanoHead> panoHead = hardwareService.getPanoHead();
    if (panoHead.isPresent()) {
      panoHead.get().setTargetVelocity(axisIndex, velocity);
    }
  }
}

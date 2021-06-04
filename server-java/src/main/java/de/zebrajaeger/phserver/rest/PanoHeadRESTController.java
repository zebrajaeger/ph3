package de.zebrajaeger.phserver.rest;

import de.zebrajaeger.phserver.PanoHeadService;
import de.zebrajaeger.phserver.data.Actor;
import de.zebrajaeger.phserver.data.PanoHeadData;
import de.zebrajaeger.phserver.hardware.HardwareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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
        return panoHeadService.isJogging();
    }

    @PutMapping("jogging")
    public boolean setJogging(@RequestParam boolean jogging) throws IOException {
        panoHeadService.setJogging(jogging);
        return panoHeadService.isJogging();
    }

    @GetMapping("actor")
    public Actor actor() {
        return panoHeadService.getData().getActor();
    }

    @PutMapping("actor/limit")
    public void limit(@RequestParam int axisIndex, @RequestParam int limit) throws IOException {
        hardwareService.getPanoHead().setLimit(axisIndex, limit);
    }

    @PutMapping("actor/pos/{axisIndex}")
    public void pos(@PathVariable int axisIndex, @RequestParam int pos) throws IOException {
        hardwareService.getPanoHead().setTargetPos(axisIndex, pos);
    }

    @PutMapping("actor/velocity/{axisIndex}")
    public void velocity(@PathVariable int axisIndex, @RequestParam int velocity) throws IOException {
        hardwareService.getPanoHead().setTargetVelocity(axisIndex, velocity);
    }
}

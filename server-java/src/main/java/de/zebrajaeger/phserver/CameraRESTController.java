package de.zebrajaeger.phserver;

import de.zebrajaeger.phserver.data.Camera;
import de.zebrajaeger.phserver.hardware.HardwareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("api")
public class CameraRESTController {

    private final HardwareService hardwareService;
    private final PanoHeadService panoHeadService;

    @Autowired
    public CameraRESTController(HardwareService hardwareService, PanoHeadService panoHeadService) {
        this.hardwareService = hardwareService;
        this.panoHeadService = panoHeadService;
    }

    @GetMapping("camera")
    public Camera data() {
        return panoHeadService.getData().getCamera();
    }

    @PutMapping("camera/focus")
    public void focus(@RequestParam int focusTimeMs) throws IOException {
        hardwareService.getPanoHead().startFocus(focusTimeMs);
    }

    @PutMapping("camera/trigger")
    public void trigger(@RequestParam int triggerTimeMs) throws IOException {
        hardwareService.getPanoHead().startTrigger(triggerTimeMs);
    }

    @PutMapping("camera/shot")
    public void trigger(@RequestParam int focusTimeMs, @RequestParam int triggerTimeMs) throws IOException {
        hardwareService.getPanoHead().startShot(focusTimeMs, triggerTimeMs);
    }

}

package de.zebrajaeger.phserver.rest;

import de.zebrajaeger.phserver.data.RobotState;
import de.zebrajaeger.phserver.service.PanoService;
import de.zebrajaeger.phserver.service.RecordService;
import de.zebrajaeger.phserver.settings.PanoFovSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class RecordRESTController {
    private final RecordService recordService;
    private final PanoService panoService;

    @Autowired
    public RecordRESTController(RecordService recordService, PanoService panoService) {
        this.recordService = recordService;
        this.panoService = panoService;
    }

    // camera
    @GetMapping("/robot/camera")
    public PanoFovSettings getCamera() {
        return panoService.getCameraFov();
    }

    @PutMapping("/robot/camera")
    public void setCamera(PanoFovSettings cameraFov) {
        PanoFovSettings cameraFOV = panoService.getCameraFov();
        cameraFOV.setX(cameraFov.getX());
        cameraFOV.setY(cameraFov.getY());
    }

    // control
    @GetMapping("/robot/state")
    public RobotState state() {
        return recordService.getRobotState();
    }

}

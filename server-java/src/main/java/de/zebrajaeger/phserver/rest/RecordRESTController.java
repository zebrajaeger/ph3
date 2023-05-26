package de.zebrajaeger.phserver.rest;

import de.zebrajaeger.phserver.service.PanoService;
import de.zebrajaeger.phserver.service.RobotService;
import de.zebrajaeger.phserver.data.RobotState;
import de.zebrajaeger.phserver.settings.DelaySettings;
import de.zebrajaeger.phserver.settings.PanoFovSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class RecordRESTController {
    private final RobotService robotService;
    private final PanoService panoService;

    @Autowired
    public RecordRESTController(RobotService robotService, PanoService panoService) {
        this.robotService = robotService;
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
        return robotService.getRobotState();
    }

//    @PutMapping("/robot/start")
//    public void start() {
//        panoService
//                .createCommands()
//                .ifPresent(robotService::start);
//    }

//    @PutMapping("/robot/stop")
//    public void stop() {
//        robotService.stop();
//    }
//
//    @PutMapping("/robot/pause")
//    public void pauseResume() {
//        robotService.PauseResume();
//    }

//    // shots
//    @GetMapping("/robot/shots")
//    public List<Shot> getShots() {
//        return panoService.getShots();
//    }
//
//    @PutMapping("/robot/shots")
//    public void setShots(List<Shot> shots) {
//        panoService.setShots(shots);
//    }
//
//    @PutMapping("/robot/shots/add")
//    public void setShots(Shot shot) {
//        panoService.getShots().add(shot);
//    }
//
//    @PutMapping("/robot/shots/clear")
//    public void clearShots() {
//        panoService.getShots().clear();
//    }

    // delay
    @GetMapping("/robot/delay")
    public DelaySettings getDelay() {
        return panoService.getDelay();
    }

    @PutMapping("/robot/delay/")
    public void setDelay(DelaySettings delay) {
        panoService.setDelay(delay);
    }

    @PutMapping("/robot/delay/waitAfterMove")
    public void delayWaitAfterMove(int waitAfterMove) {
        panoService.getDelay().setWaitAfterMove(waitAfterMove);
    }

    @PutMapping("/robot/delay/waitAfterShot")
    public void delayWaitAfterShot(int waitAfterShot) {
        panoService.getDelay().setWaitAfterShot(waitAfterShot);
    }

    @PutMapping("/robot/delay/waitBetweenShots")
    public void delayWaitBetweenShots(int waitBetweenShots) {
        panoService.getDelay().setWaitBetweenShots(waitBetweenShots);
    }
}

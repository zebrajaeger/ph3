package de.zebrajaeger.phserver.stomp;

import de.zebrajaeger.phserver.PanoService;
import de.zebrajaeger.phserver.RobotService;
import de.zebrajaeger.phserver.data.Delay;
import de.zebrajaeger.phserver.event.DelaySettingsChangedEvent;
import de.zebrajaeger.phserver.event.RobotStateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class RecordSTOMPController {

    private final PanoService panoService;
    private final RobotService robotService;
    private final SimpMessagingTemplate template;

    @Autowired
    public RecordSTOMPController(PanoService panoService, RobotService robotService, SimpMessagingTemplate template) {
        this.panoService = panoService;
        this.robotService = robotService;
        this.template = template;
    }

    //<editor-fold desc="Record">
    @MessageMapping("/record/start")
    public void start() {
        panoService
                .createCommands()
                .ifPresent(robotService::start);
    }

    @MessageMapping("/record/stop")
    public void stop() {
        robotService.stop();
    }

    @MessageMapping("/record/pause")
    public void pauseResume() {
        robotService.PauseResume();
    }

    @EventListener
    public void onRobotChanged(RobotStateEvent robotStateEvent) {
        template.convertAndSend("/topic/robot/state", robotStateEvent.getRobotState());
    }
    //</editor-fold>

    //<editor-fold desc="Delay">
    @MessageMapping("/record/delay/")
    public void setDelay(Delay delay) {
        panoService.setDelay(delay);
        panoService.publishDelayChange();
    }

    @MessageMapping("/record/delay/waitAfterMove")
    public void delayWaitAfterMove(int waitAfterMove) {
        panoService.getDelay().setWaitAfterMove(waitAfterMove);
        panoService.publishDelayChange();
    }

    @MessageMapping("/record/delay/waitAfterShot")
    public void delayWaitAfterShot(int waitAfterShot) {
        panoService.getDelay().setWaitAfterShot(waitAfterShot);
        panoService.publishDelayChange();
    }

    @MessageMapping("/record/delay/waitBetweenShots")
    public void delayWaitBetweenShots(int waitBetweenShots) {
        panoService.getDelay().setWaitBetweenShots(waitBetweenShots);
        panoService.publishDelayChange();
    }

    @EventListener
    public void onDelayChanged(DelaySettingsChangedEvent delaySettingsChangedEvent) {
        template.convertAndSend("/topic/record/delay", delaySettingsChangedEvent.getDelay());
    }
    //</editor-fold>
}

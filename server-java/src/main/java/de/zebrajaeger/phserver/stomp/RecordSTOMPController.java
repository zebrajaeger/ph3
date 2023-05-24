package de.zebrajaeger.phserver.stomp;

import de.zebrajaeger.phserver.data.Delay;
import de.zebrajaeger.phserver.data.PanoMatrix;
import de.zebrajaeger.phserver.event.DelaySettingsChangedEvent;
import de.zebrajaeger.phserver.event.RobotStateEvent;
import de.zebrajaeger.phserver.service.PanoHeadService;
import de.zebrajaeger.phserver.service.PanoService;
import de.zebrajaeger.phserver.service.RobotService;
import de.zebrajaeger.phserver.util.StompUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("api")
@Slf4j
public class RecordSTOMPController {

    private final PanoService panoService;
    private final PanoHeadService panoHeadService;
    private final RobotService robotService;
    private final SimpMessagingTemplate template;

    @Autowired
    public RecordSTOMPController(PanoService panoService, PanoHeadService panoHeadService,
                                 RobotService robotService,
                                 SimpMessagingTemplate template) {
        this.panoService = panoService;
        this.panoHeadService = panoHeadService;
        this.robotService = robotService;
        this.template = template;
    }

    //<editor-fold desc="Record">
    @MessageMapping("/record/start/{shotsName}")
    public void start(@DestinationVariable String shotsName) {
        panoHeadService.normalizeAxisPosition();
        final Optional<PanoMatrix> calculatedPano = panoService.updatePanoMatrix();
        calculatedPano.ifPresent(pano -> {
            panoService.createCommands(pano, shotsName).ifPresent(commandList -> {
                panoService.createPapywizardFile(pano);
                robotService.requestStart(commandList);
            });
        });
    }

    @MessageMapping("/record/stop")
    public void stop() {
        robotService.requestStop();
    }

    @MessageMapping("/record/pause")
    public void pauseResume() {
        robotService.requestPauseOrResume();
    }

    @MessageMapping("/rpc/robot/state")
    public void rpcRobotState(@Header("correlation-id") String id,
                              @Header("reply-to") String destination) {
        StompUtils.rpcSendResponse(template, id, destination, robotService.getRobotState());
    }

    @EventListener
    public void onRobotChanged(RobotStateEvent robotStateEvent) {
        template.convertAndSend("/topic/robot/state", robotStateEvent.robotState());
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
        template.convertAndSend("/topic/record/delay", delaySettingsChangedEvent.delay());
    }
    //</editor-fold>
}

package de.zebrajaeger.phserver.stomp;

import de.zebrajaeger.phserver.data.PanoMatrix;
import de.zebrajaeger.phserver.event.RobotStateEvent;
import de.zebrajaeger.phserver.pano.Command;
import de.zebrajaeger.phserver.service.PanoHeadService;
import de.zebrajaeger.phserver.service.PanoService;
import de.zebrajaeger.phserver.service.RecordService;
import de.zebrajaeger.phserver.util.StompUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class RecordStompController {

    private final PanoService panoService;
    private final PanoHeadService panoHeadService;
    private final RecordService recordService;
    private final SimpMessagingTemplate template;

    public RecordStompController(PanoService panoService, PanoHeadService panoHeadService,
                                 RecordService recordService,
                                 SimpMessagingTemplate template) {
        this.panoService = panoService;
        this.panoHeadService = panoHeadService;
        this.recordService = recordService;
        this.template = template;
    }

    @MessageMapping("/record/start")
    public void start() {
        panoHeadService.normalizeAxisPosition();
        final Optional<PanoMatrix> calculatedPano = panoService.updatePanoMatrix();
        calculatedPano.ifPresent(pano -> {
            List<Command> commands = panoService.createCommands(pano);
            panoService.createPapywizardFile(pano);
            recordService.requestStart(commands);
        });
    }

    @MessageMapping("/record/stop")
    public void stop() {
        recordService.requestStop();
    }

    @MessageMapping("/record/pause")
    public void pauseResume() {
        recordService.requestPauseOrResume();
    }

    @MessageMapping("/rpc/robot/state")
    public void rpcRobotState(@Header("correlation-id") String id,
                              @Header("reply-to") String destination) {
        StompUtils.rpcSendResponse(template, id, destination, recordService.getRobotState());
    }

    @EventListener
    public void onRobotChanged(RobotStateEvent robotStateEvent) {
        template.convertAndSend("/topic/robot/state", robotStateEvent.robotState());
    }
}

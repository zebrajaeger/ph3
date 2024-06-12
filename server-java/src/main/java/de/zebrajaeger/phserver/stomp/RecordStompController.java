package de.zebrajaeger.phserver.stomp;

import de.zebrajaeger.phserver.data.GpsData;
import de.zebrajaeger.phserver.data.PanoMatrix;
import de.zebrajaeger.phserver.event.GpsDataEvent;
import de.zebrajaeger.phserver.event.RobotStateEvent;
import de.zebrajaeger.phserver.pano.Command;
import de.zebrajaeger.phserver.papywizard.Papywizard;
import de.zebrajaeger.phserver.papywizard.PapywizardGenerator;
import de.zebrajaeger.phserver.service.GpsService;
import de.zebrajaeger.phserver.service.PanoHeadService;
import de.zebrajaeger.phserver.service.PanoService;
import de.zebrajaeger.phserver.service.RecordService;
import de.zebrajaeger.phserver.util.PapywizardUtils;
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
    private GpsData gpsData = null;

    public RecordStompController(PanoService panoService, PanoHeadService panoHeadService,
                                 RecordService recordService,
                                SimpMessagingTemplate template) {
        this.panoService = panoService;
        this.panoHeadService = panoHeadService;
        this.recordService = recordService;
        this.template = template;
    }

    @EventListener
    public void onGpsData(GpsDataEvent event) {
        gpsData = event.gpsData();
    }


    @MessageMapping("/record/start")
    public void start(String name) {
        panoHeadService.normalizeAxisPosition();
        final Optional<PanoMatrix> calculatedPano = panoService.updatePanoMatrix();
        calculatedPano.ifPresent(pano -> {

            final PapywizardGenerator g = new PapywizardGenerator();

            List<Command> commands = panoService.createCommands(pano);

            final Papywizard papywizard = g.generate(commands);
            papywizard.getHeader().getGeneral().setTitle(name);
            papywizard.getHeader().getGeneral().setGps(gpsData.geoLocation());
            PapywizardUtils.writePapywizardFile(papywizard, "B");

            recordService.requestStart(commands, papywizard);
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

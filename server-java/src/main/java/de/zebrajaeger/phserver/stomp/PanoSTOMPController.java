package de.zebrajaeger.phserver.stomp;

import de.zebrajaeger.phserver.data.PanoMatrix;
import de.zebrajaeger.phserver.data.Pattern;
import de.zebrajaeger.phserver.event.DelaySettingsChangedEvent;
import de.zebrajaeger.phserver.event.PanoMatrixChangedEvent;
import de.zebrajaeger.phserver.event.PatternChangedEvent;
import de.zebrajaeger.phserver.service.PanoService;
import de.zebrajaeger.phserver.settings.DelaySettings;
import de.zebrajaeger.phserver.util.StompUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@Slf4j
public class PanoSTOMPController {

    private final PanoService panoService;
    private final SimpMessagingTemplate template;

    public PanoSTOMPController(PanoService panoService, SimpMessagingTemplate template) {
        this.panoService = panoService;
        this.template = template;
    }

    //<editor-fold desc="Calculated Pano">
    @MessageMapping("/rpc/pano/matrix")
    public void rpcCalculatedPano(@Header("correlation-id") String id, @Header("reply-to") String destination) {
        panoService.updatePanoMatrix();
        Optional<PanoMatrix> panoMatrix = panoService.getPanoMatrix();
        if (panoMatrix.isPresent()) {
            StompUtils.rpcSendResponse(template, id, destination, panoMatrix.get());
        } else {
            StompUtils.rpcSendEmptyResponse(template, id, destination);
        }
    }

    @MessageMapping("/rpc/pano/recalculate")
    public void rpcRecalculatePano() {
        panoService.updatePanoMatrix();
    }

    @EventListener
    public void onCalculatedEvent(PanoMatrixChangedEvent calculatedPanoChangedEvent) {
        log.info("Calculated Pano: '{}'", calculatedPanoChangedEvent.panoMatrix());
        template.convertAndSend("/topic/pano/matrix", calculatedPanoChangedEvent.panoMatrix());
    }
    //</editor-fold>

    //<editor-fold desc="Delay">
    @MessageMapping("/delay/waitAfterMoveMs")
    public void setDelayWaitAfterMoveMs(int waitAfterMoveMs) {
        panoService.getDelay().setWaitAfterMove(waitAfterMoveMs);
        panoService.publishDelayChange();
    }

    @MessageMapping("/delay/waitBetweenShotsMs")
    public void setDelayWaitBetweenShotsMs(int waitBetweenShotsMs) {
        panoService.getDelay().setWaitBetweenShots(waitBetweenShotsMs);
        panoService.publishDelayChange();
    }

    @MessageMapping("/delay/waitAfterShotMs")
    public void setDelayWaitAfterShotMs(int waitAfterShotMs) {
        panoService.getDelay().setWaitAfterShot(waitAfterShotMs);
        panoService.publishDelayChange();
    }

    @MessageMapping("/delay")
    public void setDelay(DelaySettings delay) {
        panoService.setDelay(delay);
        panoService.publishDelayChange();
    }

    @MessageMapping("/rpc/delay")
    public void rpcDelay(@Header("correlation-id") String id, @Header("reply-to") String destination) {
        StompUtils.rpcSendResponse(template, id, destination, panoService.getDelay());
    }

    @EventListener
    public void onDelay(DelaySettingsChangedEvent delaySettingsChangedEvent) {
        template.convertAndSend("/topic/delay", delaySettingsChangedEvent.delay());
    }
    //</editor-fold>

    //<editor-fold desc="Pattern">
    @EventListener
    public void onPattern(PatternChangedEvent patternChangedEvent) {
        template.convertAndSend("/topic/pano/pattern", patternChangedEvent.pattern());
    }

    @MessageMapping("/rpc/pano/pattern")
    public void rpcPattern(@Header("correlation-id") String id, @Header("reply-to") String destination) {
        StompUtils.rpcSendResponse(template, id, destination, panoService.getPattern());
    }

    @MessageMapping("/pano/pattern")
    public void setPattern(@Payload Pattern pattern) {
        if (pattern == null) {
            pattern = Pattern.GRID;
        }
        panoService.setPattern(pattern);
        panoService.publishPatternChange();
        panoService.updatePanoMatrix();
    }

    //</editor-fold>
}

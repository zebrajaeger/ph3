package de.zebrajaeger.phserver.stomp;

import de.zebrajaeger.phserver.data.*;
import de.zebrajaeger.phserver.event.*;
import de.zebrajaeger.phserver.service.PanoService;
import de.zebrajaeger.phserver.util.StompUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("api")
public class PanoSTOMPController {

    private static final Logger LOG = LoggerFactory.getLogger(PanoSTOMPController.class);

    private final PanoService panoService;
    private final SimpMessagingTemplate template;

    @Autowired
    public PanoSTOMPController(PanoService panoService, SimpMessagingTemplate template) {
        this.panoService = panoService;
        this.template = template;
    }

    //<editor-fold desc="Pano FOV">
    @MessageMapping("/pano/border")
    public void panoBorder(Border[] borders) {
        panoService.setCurrentPositionAsPanoBorder(borders);
        panoService.publishPanoFOVChange();
        panoService.updatePanoMatrix();
    }

    @MessageMapping("/pano/fullX")
    public void panoFullX(boolean isFull) {
        panoService.getPanoFOV().setFullX(isFull);
        panoService.publishPanoFOVChange();
        panoService.updatePanoMatrix();
    }

    @MessageMapping("/pano/fullY")
    public void panoFullY(boolean isFull) {
        panoService.getPanoFOV().setFullY(isFull);
        panoService.publishPanoFOVChange();
        panoService.updatePanoMatrix();
    }

    @MessageMapping("/rpc/pano/fov")
    public void rpcPanoFov(@Header("correlation-id") String id,
                           @Header("reply-to") String destination) {
        FieldOfViewPartial fov = panoService.getPanoFOV();
        StompUtils.rpcSendResponse(template, id, destination, fov);
    }

    @EventListener
    public void onPanoFovChanged(PanoFOVChangedEvent panoFOVChangedEvent) {
        template.convertAndSend("/topic/pano/fov", panoFOVChangedEvent.panoFOV());
    }
    //</editor-fold>

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
        LOG.info("Calculated Pano: '{}'", calculatedPanoChangedEvent.panoMatrix());
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
    public void setDelay(Delay delay) {
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

    //<editor-fold desc="Shots">
    @MessageMapping("/shot/{shotsName}/{index}/focusTimeMs")
    public void setShotFocusTime(@DestinationVariable String shotsName,
                                 @DestinationVariable int index, int focusTimeMs) {
        Shot shot = panoService.getShots().getShot(shotsName, index);
        shot.setFocusTimeMs(focusTimeMs);
        panoService.publishShotsChange();
    }

    @MessageMapping("/shot/{shotsName}/{index}/triggerTimeMs")
    public void setShotTriggerTime(@DestinationVariable String shotsName,
                                   @DestinationVariable int index, int triggerTimeMs) {
        Shot shot = panoService.getShots().getShot(shotsName, index);
        shot.setTriggerTimeMs(triggerTimeMs);
        panoService.publishShotsChange();
    }

    @MessageMapping("/shot/{shotsName}/add")
    public void setShot(@DestinationVariable String shotsName, @Payload Shot shot) {
        panoService.getShots().add(shotsName, shot);
        panoService.publishShotsChange();
    }

    @MessageMapping("/rpc/shot")
    public void rpcShot(@Header("correlation-id") String id, @Header("reply-to") String destination) {
        StompUtils.rpcSendResponse(template, id, destination, panoService.getShots());
    }

    @EventListener
    public void onShots(ShotsChangedEvent shotsChangedEvent) {
        template.convertAndSend("/topic/shot", shotsChangedEvent.shots());
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

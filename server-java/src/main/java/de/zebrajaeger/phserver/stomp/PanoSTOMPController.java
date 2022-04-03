package de.zebrajaeger.phserver.stomp;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.zebrajaeger.phserver.PanoService;
import de.zebrajaeger.phserver.data.Border;
import de.zebrajaeger.phserver.data.CalculatedPano;
import de.zebrajaeger.phserver.data.Delay;
import de.zebrajaeger.phserver.data.FieldOfView;
import de.zebrajaeger.phserver.data.FieldOfViewPartial;
import de.zebrajaeger.phserver.data.Shot;
import de.zebrajaeger.phserver.event.CalculatedPanoChangedEvent;
import de.zebrajaeger.phserver.event.DelaySettingsChangedEvent;
import de.zebrajaeger.phserver.event.PanoFOVChangedEvent;
import de.zebrajaeger.phserver.event.PictureFOVChangedEvent;
import de.zebrajaeger.phserver.event.ShotsChangedEvent;
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

import java.io.IOException;
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

    //<editor-fold desc="Picture FOV">
    @MessageMapping("/picture/border")
    public void pictureBorder(Border[] borders) throws IOException {
        panoService.setCurrentPositionAsPictureBorder(borders);
        panoService.publishPictureFOVChange();
        panoService.updateCalculatedPano();
    }

    @MessageMapping("/rpc/picture/fov")
    public void rpcPictureFov(@Header("correlation-id") String id, @Header("reply-to") String destination) throws JsonProcessingException {
        FieldOfView fov = new FieldOfView(panoService.getPictureFOV());
        StompUtils.rpcSendResponse(template, id, destination, fov);
    }

    @EventListener
    public void onPictureFovChanged(PictureFOVChangedEvent pictureFOVChangedEvent) {
        template.convertAndSend("/topic/picture/fov", pictureFOVChangedEvent.getPictureFOV());
    }
    //</editor-fold>

    //<editor-fold desc="Pano FOV">
    @MessageMapping("/pano/border")
    public void panoBorder(Border[] borders) {
        panoService.setCurrentPositionAsPanoBorder(borders);
        panoService.publishPanoFOVChange();
        panoService.updateCalculatedPano();
    }

    @MessageMapping("/pano/partial")
    public void panoPartial(boolean partial) {
        panoService.getPanoFOV().setPartial(partial);
        panoService.publishPanoFOVChange();
        panoService.updateCalculatedPano();
    }

    @MessageMapping("/rpc/pano/fov")
    public void rpcPanoFov(@Header("correlation-id") String id, @Header("reply-to") String destination) throws JsonProcessingException {
        FieldOfView fov = new FieldOfViewPartial(panoService.getPanoFOV());
        StompUtils.rpcSendResponse(template, id, destination, fov);
    }

    @EventListener
    public void onPanoFovChanged(PanoFOVChangedEvent panoFOVChangedEvent) {
        template.convertAndSend("/topic/pano/fov", panoFOVChangedEvent.getPanoFOV());
    }
    //</editor-fold>

    //<editor-fold desc="Calculated Pano">
    @MessageMapping("/rpc/pano/calculated")
    public void rpcCalculatedPano(@Header("correlation-id") String id, @Header("reply-to") String destination) throws JsonProcessingException {
        Optional<CalculatedPano> calculatedPano = panoService.getCalculatedPano();
        if (calculatedPano.isPresent()) {
            StompUtils.rpcSendResponse(template, id, destination, calculatedPano.get());
        } else {
            StompUtils.rpcSendEmptyResponse(template, id, destination);
        }
    }

    @EventListener
    public void onCalculatedEvent(CalculatedPanoChangedEvent calculatedPanoChangedEvent) {
        LOG.info("Calculated Pano: '{}'", calculatedPanoChangedEvent.getCalculatedPano());
        template.convertAndSend("/topic/pano/calculated", calculatedPanoChangedEvent.getCalculatedPano());
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
    public void rpcDelay(@Header("correlation-id") String id, @Header("reply-to") String destination) throws JsonProcessingException {
        StompUtils.rpcSendResponse(template, id, destination, panoService.getDelay());
    }

    @EventListener
    public void onDelay(DelaySettingsChangedEvent delaySettingsChangedEvent) {
        template.convertAndSend("/topic/delay", delaySettingsChangedEvent.getDelay());
    }
    //</editor-fold>

    //<editor-fold desc="Shots">
    @MessageMapping("/shot/{shotsName}/{index}/focusTimeMs")
    public void setShotFocusTime(@DestinationVariable String shotsName,@DestinationVariable int index, int focusTimeMs) {
        Shot shot = panoService.getShots().getShot(shotsName, index);
        shot.setFocusTimeMs(focusTimeMs);
        panoService.publishShotsChange();
    }

    @MessageMapping("/shot/{shotsName}/{index}/triggerTimeMs")
    public void setShotTriggerTime(@DestinationVariable String shotsName,@DestinationVariable int index,int triggerTimeMs) {
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
    public void rpcShot(@Header("correlation-id") String id, @Header("reply-to") String destination) throws JsonProcessingException {
        StompUtils.rpcSendResponse(template, id, destination, panoService.getShots());
    }

    @EventListener
    public void onShots(ShotsChangedEvent shotsChangedEvent) {
        template.convertAndSend("/topic/shot", shotsChangedEvent.getShots());
    }
    //</editor-fold>
}

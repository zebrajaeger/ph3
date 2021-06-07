package de.zebrajaeger.phserver.stomp;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.zebrajaeger.phserver.PanoService;
import de.zebrajaeger.phserver.data.Border;
import de.zebrajaeger.phserver.data.CalculatedPano;
import de.zebrajaeger.phserver.data.Empty;
import de.zebrajaeger.phserver.data.FieldOfView;
import de.zebrajaeger.phserver.data.FieldOfViewPartial;
import de.zebrajaeger.phserver.data.Shot;
import de.zebrajaeger.phserver.event.CalculatedPanoChangedEvent;
import de.zebrajaeger.phserver.event.PanoFOVChangedEvent;
import de.zebrajaeger.phserver.event.PictureFOVChangedEvent;
import de.zebrajaeger.phserver.util.StompUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
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
    public void panoBorder(Border[] borders) throws IOException {
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

    //<editor-fold desc="Shots">
    @MessageMapping("/robot/shots")
    public void setShots(List<Shot> shots) {
        panoService.setShots(shots);
        panoService.publishShotsChange();
    }

    @MessageMapping("/robot/shots/add")
    public void setShots(Shot shot) {
        panoService.getShots().add(shot);
        panoService.publishShotsChange();
    }

    @MessageMapping("/robot/shots/clear")
    public void clearShots() {
        panoService.getShots().clear();
        panoService.publishShotsChange();
    }
    //</editor-fold>
}

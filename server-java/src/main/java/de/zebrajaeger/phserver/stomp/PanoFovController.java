package de.zebrajaeger.phserver.stomp;

import de.zebrajaeger.phserver.data.Border;
import de.zebrajaeger.phserver.event.PanoFOVChangedEvent;
import de.zebrajaeger.phserver.service.PanoService;
import de.zebrajaeger.phserver.settings.PanoFovSettings;
import de.zebrajaeger.phserver.util.StompUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class PanoFovController {
    private final PanoService panoService;
    private final SimpMessagingTemplate template;

    public PanoFovController(PanoService panoService, SimpMessagingTemplate template) {
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
        panoService.getPanoFov().setFullX(isFull);
        panoService.publishPanoFOVChange();
        panoService.updatePanoMatrix();
    }

    @MessageMapping("/pano/fullY")
    public void panoFullY(boolean isFull) {
        panoService.getPanoFov().setFullY(isFull);
        panoService.publishPanoFOVChange();
        panoService.updatePanoMatrix();
    }

    @MessageMapping("/rpc/pano/fov")
    public void rpcPanoFov(@Header("correlation-id") String id,
                           @Header("reply-to") String destination) {
        PanoFovSettings fov = panoService.getPanoFov();
        StompUtils.rpcSendResponse(template, id, destination, fov);
    }

    @EventListener
    public void onPanoFovChanged(PanoFOVChangedEvent panoFOVChangedEvent) {
        template.convertAndSend("/topic/pano/fov", panoFOVChangedEvent.panoFOV());
    }
}

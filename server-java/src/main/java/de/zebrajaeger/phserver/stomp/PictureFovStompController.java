package de.zebrajaeger.phserver.stomp;

import de.zebrajaeger.phserver.data.Border;
import de.zebrajaeger.phserver.event.PictureFOVChangedEvent;
import de.zebrajaeger.phserver.event.PictureFovNamesChangedEvent;
import de.zebrajaeger.phserver.service.PanoService;
import de.zebrajaeger.phserver.settings.CameraFovSettings;
import de.zebrajaeger.phserver.settings.PanoFovSettings;
import de.zebrajaeger.phserver.util.StompUtils;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class PictureFovStompController {
    private final PanoService panoService;
    private final SimpMessagingTemplate template;

    public PictureFovStompController(PanoService panoService, SimpMessagingTemplate template) {
        this.panoService = panoService;
        this.template = template;
    }

    @MessageMapping("/picture/border")
    public void pictureBorder(Border[] borders) {
        panoService.setCurrentPositionAsPictureBorder(borders);
        panoService.publishPictureFOVChange();
        panoService.updatePanoMatrix();
    }

    @MessageMapping("/rpc/picture/fov")
    public void rpcPictureFov(@Header("correlation-id") String id,
                              @Header("reply-to") String destination) {
        StompUtils.rpcSendResponse(template, id, destination, panoService.getCameraFov());
    }

    @MessageMapping("/picture/fov/load")
    public void pictureFovLoad(@Payload String name) {
        final CameraFovSettings cameraFovSettings = panoService.getPicturePresets().get(name);
        if (cameraFovSettings != null) {
            cameraFovSettings.read(panoService.getCameraFov());
            panoService.publishPictureFOVChange();
        }
    }

    @MessageMapping("/picture/fov/save")
    public void pictureFovSave(@Payload String name) {
        final PanoFovSettings fov = panoService.getCameraFov();
        if (fov.isComplete()) {
            panoService.getPicturePresets().put(
                    name,
                    new CameraFovSettings(
                            Math.abs(fov.getX().getSize()),
                            Math.abs(fov.getY().getSize())));
            panoService.publishPicturePresetsChange();
        }
    }

    @MessageMapping("/picture/fov/delete")
    public void pictureFovDelete(@Payload String name) {
        panoService.getPicturePresets().remove(name);
        panoService.publishPicturePresetsChange();
    }

    @MessageMapping("/rpc/picture/fov/names")
    public void rpcPictureFovNames(@Header("correlation-id") String id,
                                   @Header("reply-to") String destination) {
        StompUtils.rpcSendResponse(template, id, destination, panoService.getPicturePresets().keySet());
    }

    @EventListener
    public void onPictureFovNamesChanged(PictureFovNamesChangedEvent pictureFovNamesChangedEvent) {
        template.convertAndSend("/topic/picture/fov/names", pictureFovNamesChangedEvent.names());
    }

    @EventListener
    public void onPictureFovChanged(PictureFOVChangedEvent pictureFOVChangedEvent) {
        template.convertAndSend("/topic/picture/fov", pictureFOVChangedEvent.pictureFOV());
    }
}

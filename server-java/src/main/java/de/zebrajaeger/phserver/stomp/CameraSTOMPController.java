package de.zebrajaeger.phserver.stomp;

import de.zebrajaeger.phserver.event.CameraChangedEvent;
import de.zebrajaeger.phserver.hardware.actor.Camera;
import de.zebrajaeger.phserver.service.PanoHeadService;
import de.zebrajaeger.phserver.settings.ShotSettings;
import de.zebrajaeger.phserver.util.StompUtils;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;

@Controller
public class CameraSTOMPController {

    private final Camera camera;
    private final PanoHeadService panoHeadService;
    private final SimpMessagingTemplate template;

    public CameraSTOMPController(PanoHeadService deviceService, Camera camera,
                                 SimpMessagingTemplate template) {
        this.panoHeadService = deviceService;
        this.camera = camera;
        this.template = template;
    }

    @MessageMapping("/camera")
    public void getCameraStateRpc(@Header("correlation-id") String id,
                                  @Header("reply-to") String destination) {
        HashMap<String, Object> header = new HashMap<>();
        header.put("correlation-id", id);
        template.convertAndSend(destination, panoHeadService.getLatestState().getCameraStatus(), header);
    }

    @MessageMapping("camera/focus")
    public void focus(@Payload int focusTimeMs) throws Exception {
        camera.startFocus(focusTimeMs);
    }

    @MessageMapping("camera/trigger")
    public void trigger(@Payload int triggerTimeMs) throws Exception {
        camera.startTrigger(triggerTimeMs);
    }

    @MessageMapping("camera/shot")
    public void trigger(@Payload ShotSettings shot) throws Exception {
        camera.startShot(shot.getFocusTimeMs(), shot.getTriggerTimeMs());
    }

    @EventListener
    public void onCameraChanged(CameraChangedEvent cameraChangedEvent) {
        template.convertAndSend("/topic/camera", cameraChangedEvent.cameraStatus());
    }

    @MessageMapping("/rpc/camera")
    public void rpcCamera(@Header("correlation-id") String id, @Header("reply-to") String destination) {
        StompUtils.rpcSendResponse(template, id, destination, camera);
    }
}

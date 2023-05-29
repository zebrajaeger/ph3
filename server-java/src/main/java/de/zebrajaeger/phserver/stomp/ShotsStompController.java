package de.zebrajaeger.phserver.stomp;

import de.zebrajaeger.phserver.event.ShotsChangedEvent;
import de.zebrajaeger.phserver.service.PanoService;
import de.zebrajaeger.phserver.settings.ShotSettings;
import de.zebrajaeger.phserver.util.StompUtils;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShotsStompController {
    private final PanoService panoService;
    private final SimpMessagingTemplate template;

    public ShotsStompController(PanoService panoService, SimpMessagingTemplate template) {
        this.panoService = panoService;
        this.template = template;
    }

    @MessageMapping("/shot/{shotsName}/{index}/focusTimeMs")
    public void setShotFocusTime(@DestinationVariable String shotsName,
                                 @DestinationVariable int index, int focusTimeMs) {
        ShotSettings shot = panoService.getShots().getShot(shotsName, index);
        shot.setFocusTimeMs(focusTimeMs);
        panoService.publishShotsChange();
    }

    @MessageMapping("/shot/{shotsName}/{index}/triggerTimeMs")
    public void setShotTriggerTime(@DestinationVariable String shotsName,
                                   @DestinationVariable int index, int triggerTimeMs) {
        ShotSettings shot = panoService.getShots().getShot(shotsName, index);
        shot.setTriggerTimeMs(triggerTimeMs);
        panoService.publishShotsChange();
    }

    @MessageMapping("/shot/{shotsName}/add")
    public void setShot(@DestinationVariable String shotsName, @Payload ShotSettings shot) {
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
}

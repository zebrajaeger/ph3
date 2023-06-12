package de.zebrajaeger.phserver.stomp;

import de.zebrajaeger.phserver.event.DelaySettingsChangedEvent;
import de.zebrajaeger.phserver.service.DelayService;
import de.zebrajaeger.phserver.settings.DelaySettings;
import de.zebrajaeger.phserver.util.StompUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class DelayStompController {

    private final DelayService delayService;
    private final SimpMessagingTemplate template;

    public DelayStompController(DelayService delayService,
                                SimpMessagingTemplate template) {
        this.delayService = delayService;
        this.template = template;
    }

    //<editor-fold desc="Delay">
    @MessageMapping("/delay")
    public void setDelay(DelaySettings delay) {
        delayService.getDelay().write(delay);
        delayService.publishDelayChange();
    }

    @MessageMapping("/rpc/delay")
    public void rpcDelay(@Header("correlation-id") String id, @Header("reply-to") String destination) {
        StompUtils.rpcSendResponse(template, id, destination, delayService.getDelay());
    }

    @MessageMapping("/delay/waitAfterMoveMs")
    public void delayWaitAfterMove(int waitAfterMove) {
        delayService.getDelay().setWaitAfterMove(waitAfterMove);
        delayService.publishDelayChange();
    }

    @MessageMapping("/delay/waitBetweenShotsMs")
    public void delayWaitAfterShot(int waitAfterShot) {
        delayService.getDelay().setWaitAfterShot(waitAfterShot);
        delayService.publishDelayChange();
    }

    @MessageMapping("/delay/waitAfterShotMs")
    public void delayWaitBetweenShots(int waitBetweenShots) {
        delayService.getDelay().setWaitBetweenShots(waitBetweenShots);
        delayService.publishDelayChange();
    }

    @EventListener
    public void onDelayChanged(DelaySettingsChangedEvent delaySettingsChangedEvent) {
        template.convertAndSend("/topic/delay", delaySettingsChangedEvent.delay());
    }
    //</editor-fold>
}

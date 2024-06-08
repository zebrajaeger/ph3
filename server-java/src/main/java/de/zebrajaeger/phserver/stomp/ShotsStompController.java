package de.zebrajaeger.phserver.stomp;

import de.zebrajaeger.phserver.event.ShotsChangedEvent;
import de.zebrajaeger.phserver.event.ShotsPresetsChangedEvent;
import de.zebrajaeger.phserver.service.ShotService;
import de.zebrajaeger.phserver.settings.ShotsPresetSettings;
import de.zebrajaeger.phserver.settings.ShotsSettings;
import de.zebrajaeger.phserver.util.StompUtils;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShotsStompController {
    private final ShotService shotService;
    private final SimpMessagingTemplate template;

    public ShotsStompController(ShotService shotService,
                                SimpMessagingTemplate template) {
        this.shotService = shotService;
        this.template = template;
    }

    @MessageMapping("/shots/presets/set")
    public void setShotsPreset(@Payload ShotsPresetSettings presets) {
        shotService.getShotPresets().write(presets);
        shotService.publishShotPresetChange();
    }

    @MessageMapping("/rpc/shots/presets")
    public void rpcShotPreset(@Header("correlation-id") String id, @Header("reply-to") String destination) {
        StompUtils.rpcSendResponse(template, id, destination, shotService.getShotPresets());
    }

    @EventListener
    public void onShotsPreset(ShotsPresetsChangedEvent shotsPresetsChangedEvent) {
        template.convertAndSend("/topic/shots/presets", shotsPresetsChangedEvent.shots());
    }
    //</editor-fold>

    //<editor-fold desc="Current Shots">
    @MessageMapping("/shots/current/set")
    public void setShots(@Payload ShotsSettings shots) {
        shotService.getCurrent().write(shots);
        shotService.publishShotsChange();
    }

    @MessageMapping("/rpc/shots/current")
    public void rpcShot(@Header("correlation-id") String id, @Header("reply-to") String destination) {
        StompUtils.rpcSendResponse(template, id, destination, shotService.getCurrent());
    }

    @EventListener
    public void onShots(ShotsChangedEvent shotsChangedEvent) {
        template.convertAndSend("/topic/shots/current", shotsChangedEvent.shots());
    }
    //</editor-fold>
}

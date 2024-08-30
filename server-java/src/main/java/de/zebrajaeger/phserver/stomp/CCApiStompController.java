package de.zebrajaeger.phserver.stomp;

import de.zebrajaeger.phserver.event.CCApiSettingsChangedEvent;
import de.zebrajaeger.phserver.service.CCApiService;
import de.zebrajaeger.phserver.settings.CCApiSettings;
import de.zebrajaeger.phserver.util.StompUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class CCApiStompController {
    private final SimpMessagingTemplate template;
    private final CCApiService ccApiService;

    public CCApiStompController(SimpMessagingTemplate template, CCApiService ccApiService) {
        this.template = template;
        this.ccApiService = ccApiService;
    }

    @EventListener
    public void onCCApiChanged(CCApiSettingsChangedEvent ccApiSettingsChangedEvent) {
        template.convertAndSend("/topic/ccapi", ccApiSettingsChangedEvent.ccapi());
    }

    @MessageMapping("/rpc/ccapi")
    public void rpcCCApi(@Header("correlation-id") String id, @Header("reply-to") String destination) {
        StompUtils.rpcSendResponse(template, id, destination,ccApiService.getCcapi());
    }

    @MessageMapping("ccapi")
    public void setCcCApi(@Payload CCApiSettings ccApiSettings) throws Exception {
        ccApiService.getCcapi().write(ccApiSettings);
        ccApiService.publishChange();
    }
}

package de.zebrajaeger.phserver.stomp;

import de.zebrajaeger.phserver.data.GpsData;
import de.zebrajaeger.phserver.event.GpsDataEvent;
import de.zebrajaeger.phserver.service.GpsService;
import de.zebrajaeger.phserver.util.StompUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class GpsStompController {

    private final SimpMessagingTemplate template;
    private GpsData gpsData = null;

    public GpsStompController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @EventListener
    public void onGpsData(GpsDataEvent event) {
        gpsData = event.gpsData();
    }

    @MessageMapping("/rpc/gps")
    public void rpcGpsData(@Header("correlation-id") String id, @Header("reply-to") String destination) {
        StompUtils.rpcSendResponse(template, id, destination, gpsData);
    }

    @EventListener
    public void onGpsDataChanged(GpsDataEvent gpsDataEvent) {
        template.convertAndSend("/topic/gps", gpsDataEvent.gpsData());
    }
}

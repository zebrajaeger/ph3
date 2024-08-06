package de.zebrajaeger.phserver.stomp;

import de.zebrajaeger.phserver.data.GpsBrowserData;
import de.zebrajaeger.phserver.data.GpsData;
import de.zebrajaeger.phserver.data.GpsLocation;
import de.zebrajaeger.phserver.event.GpsDataEvent;
import de.zebrajaeger.phserver.util.StompUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@Slf4j
public class GpsStompController {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final SimpMessagingTemplate template;
    private GpsData gpsData = null;

    public GpsStompController(ApplicationEventPublisher applicationEventPublisher, SimpMessagingTemplate template) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.template = template;
    }

    @EventListener
    public void onGpsDataChanged(GpsDataEvent gpsDataEvent) {
        gpsData = gpsDataEvent.gpsData();
        template.convertAndSend("/topic/gps", gpsDataEvent.gpsData());
    }

    @MessageMapping("/rpc/gps")
    public void rpcGpsData(@Header("correlation-id") String id, @Header("reply-to") String destination) {
        StompUtils.rpcSendResponse(template, id, destination, gpsData);
    }

    @MessageMapping("/gps")
    public void onGpsFromBrowser(GpsBrowserData gpsBrowserData) {
        log.info("Gps from Browser: {}", gpsBrowserData);
        if (gpsBrowserData.latitude() == null || gpsBrowserData.longitude() == null) {
            log.warn("Received GPS position without lat or long: {}", gpsBrowserData);
            return;
        }
        double alt = gpsBrowserData.altitude() == null ? 0 : gpsBrowserData.altitude();
        LocalDateTime dt = gpsBrowserData.timestamp() == null
                ? LocalDateTime.now()
                : LocalDateTime.ofInstant(Instant.ofEpochMilli(gpsBrowserData.timestamp()), ZoneId.systemDefault());

        GpsLocation gpsLocation = new GpsLocation(gpsBrowserData.latitude(), gpsBrowserData.longitude(), alt);
        applicationEventPublisher.publishEvent(new GpsDataEvent(new GpsData(gpsLocation, dt)));
    }
}

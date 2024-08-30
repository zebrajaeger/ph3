package de.zebrajaeger.phserver.hardware.udp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;

import java.net.InetAddress;
import java.nio.ByteBuffer;

@Configuration
@Slf4j
@ConditionalOnProperty("enable.actor.broadcast.receiver")
public class BroadcastIntegrationConfig {

    @Value("${actor.udp.broadcast.port:1666}")
    private int port;
    private final ApplicationEventPublisher applicationEventPublisher;

    public BroadcastIntegrationConfig(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Bean
    public IntegrationFlow broadcastInboundFlow() throws Exception {
        return IntegrationFlow.from(new BroadcastMessageSource(port))
                .handle(message -> {
                    ByteBuffer bb = (ByteBuffer) message.getPayload();
                    boolean isPh5 = bb.getInt() == 0x00356870; // "ph5\0"
                    if (!isPh5) {
                        // not a ph5 message
                        return;
                    }

                    int type = bb.getInt();
                    if (type == 0x00000070) { // "p\0\0\0"
                        // present message "ph5\0p\0"
                        applicationEventPublisher.publishEvent(new Ph5PresentEvent((InetAddress) message.getHeaders().get("source")));

                    }
//                    else if (type == 0x00000073) { // "s\0\0\0"
//                        try {
//                            bb.getInt();
//                            bb.getInt();
//                            applicationEventPublisher.publishEvent(new UdpStatusEvent(bb.slice().order(bb.order())));
//                        } catch (ParseException e) {
//                            log.warn("Could not parse ph5-status UDP paket", e);
//                        }
//                    }else{
//                        log.debug("Unknown package type: {}", Integer.toHexString(type));
//                    }
                })
                .get();
    }

}

package de.zebrajaeger.phserver.hardware.udp;

import de.zebrajaeger.phserver.event.ActorStatusEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.ip.udp.UnicastReceivingChannelAdapter;

import java.nio.ByteBuffer;
import java.text.ParseException;

@Configuration
@EnableIntegration
@Slf4j
@ConditionalOnProperty("enable.actor.unicast.status.receiver")
public class UnicastConfig {

    @Value("${actor.unicast.port:1667}")
    private int port;
    private final ApplicationEventPublisher applicationEventPublisher;

    private UdpStatusEvent lastEvent = null;

    public UnicastConfig(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Bean
    public IntegrationFlow udpInboundFlow() {
        return IntegrationFlow.from(new UnicastReceivingChannelAdapter(port))
                .handle(message -> {
                    ByteBuffer bb = (ByteBuffer) message.getPayload();
                    //magic start: 'p','h','5',0
                    boolean isPh5 = bb.get() == 'p' && bb.get() == 'h' && bb.get() == '5' && bb.get() == 0;
                    if (!isPh5) {
                        // not a ph5 message
                        return;
                    }

                    byte a = bb.get();
                    byte b = bb.get();
                    if(a=='s' && b== 0){
                        // status
                    }

                    if(a=='c' && b== 0){
                        // command
                    }

                    try {
                        UdpStatusEvent event = new UdpStatusEvent(bb.slice());
                        if (lastEvent != null && lastEvent.equals(event)) {
                            return;
                        }
                        lastEvent = event;

                        applicationEventPublisher.publishEvent(new ActorStatusEvent(event.toActorStatus()));

                    } catch (ParseException e) {
                        log.debug("Could not parse UDP packet: {}", message, e);
                    }
                })
                .get();
    }
}

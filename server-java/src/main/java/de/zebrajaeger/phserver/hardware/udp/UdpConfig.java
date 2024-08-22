package de.zebrajaeger.phserver.hardware.udp;

import de.zebrajaeger.phserver.data.ActorStatus;
import de.zebrajaeger.phserver.event.ActorStatusEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.ip.udp.UnicastReceivingChannelAdapter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.text.ParseException;

@Configuration
@EnableIntegration
@Slf4j
@ConditionalOnProperty("enable.actor.udp.status")
public class UdpConfig {

    @Value("${actor.status.udp.port:12345}")
    private int port; // Port, auf dem der Server hört
    private final ApplicationEventPublisher applicationEventPublisher;

    private UdpStatusEvent lastEvent = null;

    public UdpConfig(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Bean
    public MessageChannel udpInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public UnicastReceivingChannelAdapter unicastReceivingChannelAdapter() {
        UnicastReceivingChannelAdapter adapter = new UnicastReceivingChannelAdapter(port);
        adapter.setOutputChannel(udpInputChannel());
        return adapter;
    }

    @Bean
    public IntegrationFlow udpInboundFlow() {
        return IntegrationFlow.from(udpInputChannel())
                .handle(messageHandler())
                .get();
    }

    @Bean
    @ServiceActivator(inputChannel = "udpInputChannel")
    public MessageHandler messageHandler() {
        //[112, 104, 53, 0,    1, 0, 0, 0,    0, 1, 0, 0,   0,   -109, 110, -120]
        // ID(4) x(4) y(4) active(1) unused(3)
        return message -> {
            try {
                UdpStatusEvent event = new UdpStatusEvent((byte[]) message.getPayload());
//                applicationEventPublisher.publishEvent(event);
                if(lastEvent!=null && lastEvent.equals(event)){
                    return;
                }
                lastEvent = event;

                ActorStatus data = new ActorStatus();

                data.getX().setMoving(event.isXActive());
                data.getX().setSpeed(0);
                data.getX().setPos(event.getX());

                data.getY().setMoving(event.isYActive());
                data.getY().setSpeed(0);
                data.getY().setPos(event.getY());

                applicationEventPublisher.publishEvent(new ActorStatusEvent(data));

            } catch (ParseException e) {
                log.debug("Could not parse UDP packet: {}", message, e);
            }
        };
    }

}

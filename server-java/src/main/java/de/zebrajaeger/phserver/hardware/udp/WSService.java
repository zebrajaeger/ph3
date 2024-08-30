package de.zebrajaeger.phserver.hardware.udp;

import de.zebrajaeger.phserver.data.ActorStatus;
import de.zebrajaeger.phserver.event.ActorStatusEvent;
import jakarta.websocket.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;

@Service
@Slf4j
@ClientEndpoint
public class WSService {
//
//    private final ApplicationEventPublisher applicationEventPublisher;
//    private Session session;
//    private InetAddress actorAddress;
//
//    public WSService(ApplicationEventPublisher applicationEventPublisher) {
//        this.applicationEventPublisher = applicationEventPublisher;
//    }
//
//    @EventListener
//    public void onPresent(Ph5PresentEvent e) throws DeploymentException, IOException, URISyntaxException {
//        if (actorAddress == null || !actorAddress.equals(e.location()) || session==null || !session.isOpen()) {
//            actorAddress = e.location();
//            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
//            URI uri = new URI("ws://" + actorAddress.getHostAddress() + "/ws");
//            log.info("WS connect to '{}'", uri);
//            container.connectToServer(this, uri);
//        }
//    }
//
//    @OnOpen
//    public void onOpen(Session session) {
//        log.info("[WS] connection to actor established");
//        this.session = session;
//    }
//
//    @OnClose
//    public void onClose(Session session, CloseReason closeReason) {
//        //https://www.rfc-editor.org/rfc/rfc6455#section-7.4.1
//        log.info("[WS] connection to actor closed. Reason: {}", closeReason);
//    }
//
//    @OnMessage
//    public void onMessage(ByteBuffer message) throws ParseException {
//        UdpStatusEvent e = UdpStatusEvent.of(message.order(ByteOrder.LITTLE_ENDIAN));
//        ActorStatus as = new ActorStatus();
//        as.getX().setPos(e.getX());
//        as.getX().setMoving(e.isXActive());
//        as.getY().setPos(e.getY());
//        as.getY().setMoving(e.isYActive());
//        ActorStatusEvent asw = new ActorStatusEvent(as);
//
//        applicationEventPublisher.publishEvent(asw);
//    }

    public void sendCommand(UdpCommand cmd) {
//        if (session == null || !session.isOpen()) {
//            log.warn("WS connection not available");
//        }
//        session.getAsyncRemote().sendBinary(cmd.asByteBuffer());
    }
}

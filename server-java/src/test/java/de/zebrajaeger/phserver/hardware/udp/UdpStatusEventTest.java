package de.zebrajaeger.phserver.hardware.udp;

import jakarta.websocket.DeploymentException;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class UdpStatusEventTest {
    @Test
    public void foo() {
        // 123456
        System.out.println(Integer.toHexString(123456));
        ByteBuffer bb = ByteBuffer.wrap(new byte[]{0x00, 0x00, 0x00, 0x00, 0x40, (byte) 0xe2, 0x01, 0x00}).order(ByteOrder.LITTLE_ENDIAN);
        bb.getInt();
        ByteBuffer x = bb.slice().order(ByteOrder.LITTLE_ENDIAN);
        System.out.println(Integer.toHexString(bb.getInt()));
    }


//    @Test
//    public void bar() throws InterruptedException, URISyntaxException, DeploymentException, IOException {
//        SimpleWebSocketClient client = new SimpleWebSocketClient();
//
//        client.connect(new URI("ws://192.168.178.90:80/ws"));
//
//        while (!client.isOpen()) {
//            Thread.sleep(10);
//        }
//        UdpCommand cmd = new UdpCommand();
//        cmd.setId(0);
//        cmd.setType(CommandType.PING);
//        cmd.setPosX(1);
//        cmd.setSpeedX(2);
//        cmd.setPosY(3);
//        cmd.setSpeedY(4);
//
//        client.sendMessage(cmd.asByteBuffer());
//
//        Thread.sleep(5000000);
//
//    }
}
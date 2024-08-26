package de.zebrajaeger.phserver.hardware.udp;

import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BroadcastMessageSource implements MessageSource<ByteBuffer> {

    private final DatagramSocket socket;

    public BroadcastMessageSource(int port) throws Exception {
        this.socket = new DatagramSocket(port);
        this.socket.setBroadcast(true);
    }

    @Override
    public Message<ByteBuffer> receive() {
        try {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            return MessageBuilder
                    .withPayload(ByteBuffer.wrap(packet.getData()).order(ByteOrder.LITTLE_ENDIAN))
                    .setHeader("source", packet.getAddress())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Empfang des Broadcast-Pakets", e);
        }
    }
}

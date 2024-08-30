package de.zebrajaeger.phserver.hardware.udp;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class BroadcastMessageSource implements MessageSource<ByteBuffer> {

    private final DatagramSocket socket;
    private final ExecutorService executorService;
    private final BlockingQueue<Message<ByteBuffer>> messageQueue;

    public BroadcastMessageSource(int port) throws Exception {
        socket = new DatagramSocket(port);
        socket.setBroadcast(true);

        executorService = Executors.newSingleThreadExecutor();
        messageQueue = new LinkedBlockingQueue<>(100);
        startReceiving();
    }


    private void startReceiving() {
        executorService.submit(() -> {
            while (!socket.isClosed()) {
                try {
                    byte[] buffer = new byte[48];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);  // Blockierende Operation im Hintergrund

                    Message<ByteBuffer> message = MessageBuilder
                            .withPayload(ByteBuffer.wrap(packet.getData(), 0, packet.getLength()).order(ByteOrder.LITTLE_ENDIAN))
                            .setHeader("source", packet.getAddress())
                            .build();

                    messageQueue.add(message);
                } catch (Exception e) {
                    if (!socket.isClosed()) {
                        log.error("Error during receiving a Broadcast-Packet", e);
                    }
                }
            }
        });
    }

    @Override
    public Message<ByteBuffer> receive() {
//        try {
//            byte[] buffer = new byte[48];
//            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
//            socket.receive(packet);
//
//            return MessageBuilder
//                    .withPayload(ByteBuffer.wrap(packet.getData(), 0, packet.getLength()).order(ByteOrder.LITTLE_ENDIAN))
//                    .setHeader("source", packet.getAddress())
//                    .build();
//        } catch (Exception e) {
//            throw new RuntimeException("Fehler beim Empfang des Broadcast-Pakets", e);
//        }
        return messageQueue.poll();
    }

    @PreDestroy
    public void shutdown() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        executorService.shutdownNow();
    }
}

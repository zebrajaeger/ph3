package de.zebrajaeger.phserver.hardware.udp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Setter
@Getter
@ToString
public class UdpCommand {
    private int id;
    private CommandType type;
    private int posX;
    private int speedX;
    private int posY;
    private int speedY;

    public static UdpCommand of(ByteBuffer bb) {
        UdpCommand result = new UdpCommand();
        result.id = bb.getInt();
        result.type = CommandType.valueOf(bb.getInt()).orElse(CommandType.UNKNOWN);
        result.posX = bb.getInt();
        result.speedX = bb.getInt();
        result.posY = bb.getInt();
        result.speedY = bb.getInt();
        return result;
    }

    public ByteBuffer asByteBuffer() {
        byte[] bytes = new byte[6 * 4];
        ByteBuffer bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(id);
        bb.putInt(type.getValue());
        bb.putInt(posX);
        bb.putInt(speedX);
        bb.putInt(posY);
        bb.putInt(speedY);
        bb.position(0);
        return bb;
    }

    public byte[] asByteArray() {
        byte[] bytes = new byte[6 * 4];
        ByteBuffer bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(id);
        bb.putInt(type.getValue());
        bb.putInt(posX);
        bb.putInt(speedX);
        bb.putInt(posY);
        bb.putInt(speedY);
        return bytes;
    }

    public DatagramPacket asDatagram() {
        byte[] ba = asByteArray();
        return new DatagramPacket(ba, ba.length);
    }
}

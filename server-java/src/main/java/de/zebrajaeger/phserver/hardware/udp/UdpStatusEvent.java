package de.zebrajaeger.phserver.hardware.udp;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.util.BitSet;

@Getter
@ToString
@EqualsAndHashCode
public class UdpStatusEvent {
    int x;
    int y;
    boolean xActive;
    boolean yActive;

    public UdpStatusEvent(byte[] msg) throws ParseException {
        ByteBuffer buffer = ByteBuffer.wrap(msg).order(ByteOrder.LITTLE_ENDIAN);
        try {
            // must start with ph5
            if (buffer.get() != 'p' || buffer.get() != 'h' || buffer.get() != '5' || buffer.get() != 0) {
                throw new ParseException("Not a 'ph5' message", -1);
            }
            x = buffer.getInt();
            y = buffer.getInt();
            byte b = buffer.get();
            BitSet bitSet = BitSet.valueOf(new byte[]{b});
            xActive = bitSet.get(0);
            yActive = bitSet.get(1);
        } catch (BufferUnderflowException e) {
            throw new ParseException("Message to short", -1);
        }
    }
}

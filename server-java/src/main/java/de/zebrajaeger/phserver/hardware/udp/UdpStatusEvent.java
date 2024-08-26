package de.zebrajaeger.phserver.hardware.udp;

import de.zebrajaeger.phserver.data.ActorStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
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

    public UdpStatusEvent(ByteBuffer buffer) throws ParseException {
        // [1, 0, 0, 0,    0, 1, 0, 0,   0,   -109, 110, -120]
        // x(4) y(4) active(1) unused(3)
        try {
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

    public ActorStatus toActorStatus() {
        ActorStatus data = new ActorStatus();

        data.getX().setMoving(isXActive());
        data.getX().setSpeed(0);
        data.getX().setPos(getX());

        data.getY().setMoving(isYActive());
        data.getY().setSpeed(0);
        data.getY().setPos(getY());
        return data;
    }
}

package de.zebrajaeger.phserver.hardware;

import com.pi4j.io.i2c.I2CDevice;
import de.zebrajaeger.phserver.PanoHead;
import de.zebrajaeger.phserver.data.Actor;
import de.zebrajaeger.phserver.data.PanoHeadData;

import java.io.IOException;
import java.nio.ByteBuffer;

public class I2cPanoHead implements PanoHead {
    private final I2CDevice i2CDevice;

    public I2cPanoHead(I2CDevice i2CDevice) {
        this.i2CDevice = i2CDevice;
    }

    public PanoHeadData read() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(14);
        i2CDevice.read(buffer.array(), 0, buffer.capacity());

        PanoHeadData result = new PanoHeadData();

        result.setMovementRaw(buffer.get());
        Actor actor = result.getActor();
        actor.getX().setPos(buffer.getInt());
        actor.getX().setSpeed(buffer.getShort());
        actor.getY().setPos(buffer.getInt());
        actor.getY().setSpeed(buffer.getShort());
        result.setCameraRaw(buffer.get());

        result.init();
        return result;
    }

    public void startFocus(int focusTimeMs) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(5);
        buffer.put((byte) 0x30);
        buffer.putInt(focusTimeMs);
        i2CDevice.write(buffer.array(), 0, buffer.capacity());
    }

    public void startTrigger(int triggerTimeMs) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(5);
        buffer.put((byte) 0x31);
        buffer.putInt(triggerTimeMs);
        i2CDevice.write(buffer.array(), 0, buffer.capacity());
    }

    public void startShot(int focusTimeMs, int triggerTimeMs) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(9);
        buffer.put((byte) 0x32);
        buffer.putInt(focusTimeMs);
        buffer.putInt(triggerTimeMs);
        i2CDevice.write(buffer.array(), 0, buffer.capacity());
    }

    public void setLimit(int axis, int limit) throws IOException {
        if (axis == 0 || axis == 1) {
            ByteBuffer buffer = ByteBuffer.allocate(6);
            buffer.put((byte) 0x20);
            buffer.put((byte) axis);
            buffer.putInt(limit);
            i2CDevice.write(buffer.array(), 0, buffer.capacity());
        } else {
            throw new IllegalArgumentException("Wrong axis index: " + axis);
        }
    }

    public void setTargetVelocity(int axis, int velocity) throws IOException {
        if (axis == 0 || axis == 1) {
            ByteBuffer buffer = ByteBuffer.allocate(6);
            buffer.put((byte) 0x21);
            buffer.put((byte) axis);
            buffer.putInt(velocity);
            i2CDevice.write(buffer.array(), 0, buffer.capacity());
        } else {
            throw new IllegalArgumentException("Wrong axis index: " + axis);
        }
    }

    public void setTargetPos(int axis, int pos) throws IOException {
        if (axis == 0 || axis == 1) {
            ByteBuffer buffer = ByteBuffer.allocate(6);
            buffer.put((byte) 0x22);
            buffer.put((byte) axis);
            buffer.putInt(pos);
            i2CDevice.write(buffer.array(), 0, buffer.capacity());
        } else {
            throw new IllegalArgumentException("Wrong axis index: " + axis);
        }
    }
}

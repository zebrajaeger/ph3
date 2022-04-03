package de.zebrajaeger.phserver.hardware;

import de.zebrajaeger.phserver.data.Actor;
import de.zebrajaeger.phserver.data.PanoHeadData;
import de.zebrajaeger.phserver.util.NumberConverter;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <pre>
 * enum command_t {
 *   stepperWriteLimit = 0x20,
 *   stepperWriteVelocity = 0x21,
 *   stepperWriteTargetPos = 0x22,
 *   stepperStopAll = 0x23,
 *
 *   stepperWriteActualPos = 0x28,
 *   stepperResetPos = 0x29,
 *
 *   cameraStartFocus = 0x30,
 *   cameraStartTrigger = 0x31,
 *   cameraStartShot = 0x32,
 *
 *   unknown = 127
 * };
 * </pre>
 */
public class PanoHeadDevice implements PanoHead {
    private final HardwareDevice hardwareDevice;

    public PanoHeadDevice(HardwareDevice hardwareDevice) {
        this.hardwareDevice = hardwareDevice;
    }

    public PanoHeadData read() throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(hardwareDevice.read(14)).order(ByteOrder.LITTLE_ENDIAN);

        PanoHeadData result = new PanoHeadData();

        result.setMovementRaw(buffer.get());
        Actor actor = result.getActor();
        actor.getX().setPos(NumberConverter.unsigned24ToSigned(buffer.getInt()));
        actor.getX().setSpeed(NumberConverter.unsigned16ToSigned(buffer.getShort()));
        actor.getY().setPos(NumberConverter.unsigned24ToSigned(buffer.getInt()));
        actor.getY().setSpeed(NumberConverter.unsigned16ToSigned(buffer.getShort()));
        result.setCameraRaw(buffer.get());

        result.init();
        return result;
    }

    public void startFocus(int focusTimeMs) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 0x30);
        buffer.putInt(focusTimeMs);
        hardwareDevice.write(buffer.array());
    }

    public void startTrigger(int triggerTimeMs) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 0x31);
        buffer.putInt(triggerTimeMs);
        hardwareDevice.write(buffer.array());
    }

    public void startShot(int focusTimeMs, int triggerTimeMs) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(9).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 0x32);
        buffer.putInt(focusTimeMs);
        buffer.putInt(triggerTimeMs);
        hardwareDevice.write(buffer.array());
    }

    public void setLimit(int axisIndex, int limit) throws IOException {
        Assert.state(axisIndex == 0 || axisIndex == 1, "Illegal axis index: " + axisIndex);
        ByteBuffer buffer = ByteBuffer.allocate(6).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 0x20);
        buffer.put((byte) axisIndex);
        buffer.putInt(NumberConverter.int32toInt24(limit));
        hardwareDevice.write(buffer.array());
    }

    public void setTargetVelocity(int axisIndex, int velocity) throws IOException {
        Assert.state(axisIndex == 0 || axisIndex == 1, "Illegal axis index: " + axisIndex);
        ByteBuffer buffer = ByteBuffer.allocate(6).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 0x21);
        buffer.put((byte) axisIndex);
        buffer.putInt(NumberConverter.int32toInt24(velocity));
        hardwareDevice.write(buffer.array());
    }

    public void setTargetPos(int axisIndex, int pos) throws IOException {
        Assert.state(axisIndex == 0 || axisIndex == 1, "Illegal axis index: " + axisIndex);
        ByteBuffer buffer = ByteBuffer.allocate(6).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 0x22);
        buffer.put((byte) axisIndex);
        buffer.putInt(NumberConverter.int32toInt24(pos));
        hardwareDevice.write(buffer.array());
    }

    public void stopAll() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put((byte) 0x23);
        hardwareDevice.write(buffer.array());
    }

    public void setActualPos(int axisIndex, int pos) throws IOException {
        Assert.state(axisIndex == 0 || axisIndex == 1, "Illegal axis index: " + axisIndex);
        ByteBuffer buffer = ByteBuffer.allocate(6).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 0x28);
        buffer.put((byte) axisIndex);
        buffer.putInt(NumberConverter.int32toInt24(pos));
        hardwareDevice.write(buffer.array());
    }

    public void resetPos() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put((byte) 0x29);
        hardwareDevice.write(buffer.array());
    }
}

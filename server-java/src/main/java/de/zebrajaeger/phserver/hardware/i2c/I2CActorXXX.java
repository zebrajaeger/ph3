package de.zebrajaeger.phserver.hardware.i2c;

import de.zebrajaeger.phserver.data.ActorStatus;
import de.zebrajaeger.phserver.data.AxisIndex;
import de.zebrajaeger.phserver.data.PanoHeadData;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import de.zebrajaeger.phserver.hardware.poll.PollingActor;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;

/**
 * <pre>
 * enum command_t {
 *   stepperWriteLimit = 0x20,
 *   stepperWriteVelocity = 0x21,
 *   stepperWriteTargetPos = 0x22,
 *   stepperStopAll = 0x23,
 *
 *   stepperWriteActualAndTargetPos = 0x27,
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
//@Service
//@Slf4j
//@Profile({"locali2c","remotei2c"})
@Deprecated
public class I2CActorXXX {

//    private static final boolean X_INVERTED = true;
//    private static final boolean Y_INVERTED = false;
//    @Value("${i2c.address.panohead:0x33}")
//    private int i2cAddress;
//    private final I2CDeviceFactory deviceFactory;
//    private I2CDevice i2CDevice;
//
//    public I2CPanoHead(ApplicationEventPublisher applicationEventPublisher, I2CDeviceFactory deviceFactory) {
//        super(applicationEventPublisher);
//        this.deviceFactory = deviceFactory;
//    }
//    @PostConstruct
//    public void init() throws Exception {
//        i2CDevice = deviceFactory.create(i2cAddress);
//    }
//
//    public PanoHeadData read() throws IOException {
//        ByteBuffer buffer = ByteBuffer.wrap(i2CDevice.read(20)).order(ByteOrder.LITTLE_ENDIAN);
//
//        PanoHeadData result = new PanoHeadData();
//
//        result.setMovementRaw(buffer.get());
//        ActorStatus actorStatus = result.getActorStatus();
//        actorStatus.getX().setPos(checkAndInvertIfNeeded(AxisIndex.X, buffer.getInt()));
//        actorStatus.getX().setSpeed(checkAndInvertIfNeeded(AxisIndex.X, buffer.getShort()));
//        actorStatus.getY().setPos(checkAndInvertIfNeeded(AxisIndex.Y, buffer.getInt()));
//        actorStatus.getY().setSpeed(checkAndInvertIfNeeded(AxisIndex.Y, buffer.getShort()));
//        actorStatus.getZ().setPos(checkAndInvertIfNeeded(AxisIndex.Z, buffer.getInt()));
//        actorStatus.getZ().setSpeed(checkAndInvertIfNeeded(AxisIndex.Z, buffer.getShort()));
//        result.setCameraRaw(buffer.get());
//
//        result.init();
//        return result;
//    }
//
//    public void startFocus(int focusTimeMs) throws IOException {
//        ByteBuffer buffer = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN);
//        buffer.put((byte) 0x30);
//        buffer.putInt(focusTimeMs);
//        i2CDevice.write(buffer.array());
//    }
//
//    public void startTrigger(int triggerTimeMs) throws IOException {
//        ByteBuffer buffer = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN);
//        buffer.put((byte) 0x31);
//        buffer.putInt(triggerTimeMs);
//        i2CDevice.write(buffer.array());
//    }
//
//    public void startShot(int focusTimeMs, int triggerTimeMs) throws IOException {
//        ByteBuffer buffer = ByteBuffer.allocate(9).order(ByteOrder.LITTLE_ENDIAN);
//        buffer.put((byte) 0x32);
//        buffer.putInt(focusTimeMs);
//        buffer.putInt(triggerTimeMs);
//        i2CDevice.write(buffer.array());
//    }
//
//    public void setLimit(AxisIndex axisIndex, int velocityMinHz, int velocityMaxHz, int accelerationMaxHzPerSecond) throws IOException {
//        log.info("setLimit {}: min:{} max{} acc:{}", axisIndex, velocityMinHz, velocityMaxHz, accelerationMaxHzPerSecond);
//        ByteBuffer buffer = ByteBuffer.allocate(14).order(ByteOrder.LITTLE_ENDIAN);
//        buffer.put((byte) 0x20);
//        buffer.put((byte) axisIndexToIndex(axisIndex));
//        buffer.putInt(velocityMinHz);
//        buffer.putInt(velocityMaxHz);
//        buffer.putInt(accelerationMaxHzPerSecond);
//        i2CDevice.write(buffer.array());
//    }
//
//    public void setTargetVelocity(AxisIndex axisIndex, int velocity) throws IOException {
//        log.info("SetVelocity {}: {}", axisIndex, velocity);
//        velocity = checkAndInvertIfNeeded(axisIndex, velocity);
//        ByteBuffer buffer = ByteBuffer.allocate(6).order(ByteOrder.LITTLE_ENDIAN);
//        buffer.put((byte) 0x21);
//        buffer.put((byte) axisIndexToIndex(axisIndex));
//        buffer.putInt(velocity);
//        i2CDevice.write(buffer.array());
//    }
//
//    public void setTargetPos(AxisIndex axisIndex, int pos) throws IOException {
//        pos = checkAndInvertIfNeeded(axisIndex, pos);
//        ByteBuffer buffer = ByteBuffer.allocate(6).order(ByteOrder.LITTLE_ENDIAN);
//        buffer.put((byte) 0x22);
//        buffer.put((byte) axisIndexToIndex(axisIndex));
//        buffer.putInt(pos);
//        i2CDevice.write(buffer.array());
//    }
//
//    public void stopAll() throws IOException {
//        ByteBuffer buffer = ByteBuffer.allocate(1);
//        buffer.put((byte) 0x23);
//        i2CDevice.write(buffer.array());
//    }
//
//    public void setActualAndTargetPos(AxisIndex axisIndex, int pos) throws IOException {
//        pos = checkAndInvertIfNeeded(axisIndex, pos);
//        ByteBuffer buffer = ByteBuffer.allocate(6).order(ByteOrder.LITTLE_ENDIAN);
//        buffer.put((byte) 0x27);
//        buffer.put((byte) axisIndexToIndex(axisIndex));
//        buffer.putInt(pos);
//        i2CDevice.write(buffer.array());
//    }
//
//    public void setActualPos(AxisIndex axisIndex, int pos) throws IOException {
//        pos = checkAndInvertIfNeeded(axisIndex, pos);
//        ByteBuffer buffer = ByteBuffer.allocate(6).order(ByteOrder.LITTLE_ENDIAN);
//        buffer.put((byte) 0x28);
//        buffer.put((byte) axisIndexToIndex(axisIndex));
//        buffer.putInt(pos);
//        i2CDevice.write(buffer.array());
//    }
//
//    public void resetPos() throws IOException {
//        ByteBuffer buffer = ByteBuffer.allocate(1);
//        buffer.put((byte) 0x29);
//        i2CDevice.write(buffer.array());
//    }
//
//    private int checkAndInvertIfNeeded(AxisIndex axisIndex, int value) {
//        if (X_INVERTED && axisIndex == AxisIndex.X) {
//            value = -value;
//        }
//        if (Y_INVERTED && axisIndex == AxisIndex.Y) {
//            value = -value;
//        }
//        return value;
//    }
//
//    private int axisIndexToIndex(AxisIndex axisIndex) {
//        return switch (axisIndex) {
//            case X -> 0;
//            case Y -> 2;
//            default -> throw new RuntimeException("Unexpected Axis :" + axisIndex);
//        };
//    }
}

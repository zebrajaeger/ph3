package de.zebrajaeger.phserver.hardware.actor;

import de.zebrajaeger.phserver.data.ActorStatus;
import de.zebrajaeger.phserver.data.AxisIndex;
import de.zebrajaeger.phserver.data.PanoHeadData;
import de.zebrajaeger.phserver.hardware.i2c.I2CDevice;
import de.zebrajaeger.phserver.hardware.i2c.I2CDeviceFactory;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Service
@Profile({"locali2c", "remotei2c"})
@Slf4j
public class I2CActor extends PollingActor implements Actor {
    private static final boolean X_INVERTED = true;
    private static final boolean Y_INVERTED = false;

    private final I2CDeviceFactory deviceFactory;
    @Value("${i2c.address.panohead:0x33}")
    private int i2cAddress;
    private I2CDevice i2CDevice;

    public I2CActor(ApplicationEventPublisher applicationEventPublisher, I2CDeviceFactory deviceFactory) {
        super(applicationEventPublisher);
        this.deviceFactory = deviceFactory;
    }

    @PostConstruct
    public void init() throws Exception {
        i2CDevice = deviceFactory.create(i2cAddress);
    }

    @Override
    public PanoHeadData read() throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(i2CDevice.read(20)).order(ByteOrder.LITTLE_ENDIAN);

        PanoHeadData result = new PanoHeadData();

        result.setMovementRaw(buffer.get());
        ActorStatus actorStatus = result.getActorStatus();
        actorStatus.getX().setPos(checkAndInvertIfNeeded(AxisIndex.X, buffer.getInt()));
        actorStatus.getX().setSpeed(checkAndInvertIfNeeded(AxisIndex.X, buffer.getShort()));
        actorStatus.getY().setPos(checkAndInvertIfNeeded(AxisIndex.Y, buffer.getInt()));
        actorStatus.getY().setSpeed(checkAndInvertIfNeeded(AxisIndex.Y, buffer.getShort()));
        actorStatus.getZ().setPos(checkAndInvertIfNeeded(AxisIndex.Z, buffer.getInt()));
        actorStatus.getZ().setSpeed(checkAndInvertIfNeeded(AxisIndex.Z, buffer.getShort()));
        result.setCameraRaw(buffer.get());

        result.init();
        return result;
    }

    @Override
    public void setActualAndTargetPos(AxisIndex axisIndex, int pos) throws IOException {
        pos = checkAndInvertIfNeeded(axisIndex, pos);
        ByteBuffer buffer = ByteBuffer.allocate(6).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 0x27);
        buffer.put((byte) axisIndexToIndex(axisIndex));
        buffer.putInt(pos);
        i2CDevice.write(buffer.array());
    }

    @Override
    public void startFocus(int focusTimeMs) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 0x30);
        buffer.putInt(focusTimeMs);
        i2CDevice.write(buffer.array());
    }

    @Override
    public void startTrigger(int triggerTimeMs) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 0x31);
        buffer.putInt(triggerTimeMs);
        i2CDevice.write(buffer.array());
    }

    @Override
    public void startShot(int focusTimeMs, int triggerTimeMs) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(9).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 0x32);
        buffer.putInt(focusTimeMs);
        buffer.putInt(triggerTimeMs);
        i2CDevice.write(buffer.array());
    }

    @Override
    public void setLimit(AxisIndex axisIndex, int velocityMinHz, int velocityMaxHz, int accelerationMaxHzPerSecond) throws Exception {
        log.info("setLimit {}: min:{} max{} acc:{}", axisIndex, velocityMinHz, velocityMaxHz, accelerationMaxHzPerSecond);
        ByteBuffer buffer = ByteBuffer.allocate(14).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 0x20);
        buffer.put((byte) axisIndexToIndex(axisIndex));
        buffer.putInt(velocityMinHz);
        buffer.putInt(velocityMaxHz);
        buffer.putInt(accelerationMaxHzPerSecond);
        i2CDevice.write(buffer.array());
    }

    @Override
    public void setTargetVelocity(AxisIndex axisIndex, int velocity) throws IOException {
        log.info("SetVelocity {}: {}", axisIndex, velocity);
        velocity = checkAndInvertIfNeeded(axisIndex, velocity);
        ByteBuffer buffer = ByteBuffer.allocate(6).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 0x21);
        buffer.put((byte) axisIndexToIndex(axisIndex));
        buffer.putInt(velocity);
        i2CDevice.write(buffer.array());
    }

    @Override
    public void setTargetPos(AxisIndex axisIndex, int pos) throws IOException {
        pos = checkAndInvertIfNeeded(axisIndex, pos);
        ByteBuffer buffer = ByteBuffer.allocate(6).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 0x22);
        buffer.put((byte) axisIndexToIndex(axisIndex));
        buffer.putInt(pos);
        i2CDevice.write(buffer.array());
    }

    @Override
    public void stopAll() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put((byte) 0x23);
        i2CDevice.write(buffer.array());
    }

    private int checkAndInvertIfNeeded(AxisIndex axisIndex, int value) {
        if (X_INVERTED && axisIndex == AxisIndex.X) {
            value = -value;
        }
        if (Y_INVERTED && axisIndex == AxisIndex.Y) {
            value = -value;
        }
        return value;
    }

    private int axisIndexToIndex(AxisIndex axisIndex) {
        return switch (axisIndex) {
            case X -> 0;
            case Y -> 2;
            default -> throw new RuntimeException("Unexpected Axis :" + axisIndex);
        };
    }
}

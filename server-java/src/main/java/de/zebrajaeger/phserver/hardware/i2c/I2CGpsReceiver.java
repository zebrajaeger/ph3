package de.zebrajaeger.phserver.hardware.i2c;

import de.zebrajaeger.phserver.data.GpsData;
import de.zebrajaeger.phserver.data.GpsFlags;
import de.zebrajaeger.phserver.data.GpsLocation;
import de.zebrajaeger.phserver.data.GpsMetaData;
import de.zebrajaeger.phserver.hardware.poll.PollingGpsReceiver;
import de.zebrajaeger.phserver.hardware.local.LocalI2cDeviceFactory;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDateTime;

@Component
@Profile({"locali2c", "remotei2c"})
@Slf4j
public class I2CGpsReceiver extends PollingGpsReceiver {
    private final I2CDeviceFactory deviceFactory;
    @Value("${i2c.address.gps:0x34}")
    private int i2cAddress;
    private I2CDevice i2CDevice;

    public I2CGpsReceiver(ApplicationEventPublisher applicationEventPublisher, I2CDeviceFactory deviceFactory) {
        super(applicationEventPublisher);
        this.deviceFactory = deviceFactory;
    }

    @PostConstruct
    public void init() throws Exception {
        i2CDevice = deviceFactory.create(i2cAddress);
    }

    public GpsData read() throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(i2CDevice.read(29)).order(ByteOrder.LITTLE_ENDIAN);

        final GpsLocation geoLocation = new GpsLocation(buffer.getFloat(), buffer.getFloat(), buffer.getFloat());

        LocalDateTime dateTime;
        try {
            dateTime = LocalDateTime.of(
                    buffer.getShort(), buffer.get(), buffer.get(),
                    buffer.get(), buffer.get(), buffer.get());
        } catch (java.time.DateTimeException e) {
            log.debug("Could not read Gps-time", e);
            dateTime = LocalDateTime.now();
        }

        return new GpsData(
                geoLocation,
                dateTime,
                new GpsMetaData(buffer.getInt(), buffer.getInt()),
                toGpsFlags(buffer.get()),
                toGpsFlags(buffer.get())
        );
    }

    private GpsFlags toGpsFlags(short flags) {
        return new GpsFlags(
                (flags & 0x01) != 0,
                (flags & 0x02) != 0,
                (flags & 0x04) != 0,
                (flags & 0x08) != 0,
                (flags & 0x10) != 0,
                (flags & 0x20) != 0);
    }
}

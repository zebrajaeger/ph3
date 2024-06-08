package de.zebrajaeger.phserver.hardware.pi;

import de.zebrajaeger.phserver.data.GpsData;
import de.zebrajaeger.phserver.data.GpsFlags;
import de.zebrajaeger.phserver.data.GpsLocation;
import de.zebrajaeger.phserver.data.GpsMetaData;
import de.zebrajaeger.phserver.hardware.GpsDevice;
import de.zebrajaeger.phserver.hardware.HardwareDevice;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDateTime;

@Slf4j
public class PiGpsDevice implements GpsDevice {
    private final HardwareDevice hardwareDevice;

    public PiGpsDevice(HardwareDevice hardwareDevice) {
        this.hardwareDevice = hardwareDevice;
    }

    public GpsData read() throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(hardwareDevice.read(29)).order(ByteOrder.LITTLE_ENDIAN);

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

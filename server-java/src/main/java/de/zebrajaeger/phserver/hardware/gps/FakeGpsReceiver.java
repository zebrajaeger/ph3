package de.zebrajaeger.phserver.hardware.gps;

import de.zebrajaeger.phserver.data.GpsData;
import de.zebrajaeger.phserver.data.GpsFlags;
import de.zebrajaeger.phserver.data.GpsLocation;
import de.zebrajaeger.phserver.data.GpsMetaData;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@Profile({"develop"})
public class FakeGpsReceiver extends PollingGpsReceiver {

    public FakeGpsReceiver(ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
    }

    @Override
    public GpsData read() throws IOException {
        return new GpsData(
                new GpsLocation(53.555199146125446f, 9.996066819046005f, 25f),
                LocalDateTime.now(),
                new GpsMetaData(5, 500),
                new GpsFlags(true, true, true, true, true, true),
                new GpsFlags(true, true, true, true, true, true));
    }
}

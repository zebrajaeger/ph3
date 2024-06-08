package de.zebrajaeger.phserver.data;

import java.time.LocalDateTime;

public record GpsData(GpsLocation geoLocation,
               LocalDateTime dateTime,
               GpsMetaData gpsMetaData,
               GpsFlags valid,
               GpsFlags updated) {
}

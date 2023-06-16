package de.zebrajaeger.phserver.data;

public record GpsFlags(
        boolean location, boolean altitude,
        boolean date, boolean time,
        boolean satellites, boolean hdop) {
}

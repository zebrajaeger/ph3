package de.zebrajaeger.phserver.hardware;

import java.util.Optional;

public interface HardwareService {
    PanoHead getPanoHead();

    PowerGauge getPowerGauge();

    Optional<AccelerationSensor> getAccelerationSensor();

    SystemDevice getSystemDevice();

    GpsDevice getGpsDevice();
}

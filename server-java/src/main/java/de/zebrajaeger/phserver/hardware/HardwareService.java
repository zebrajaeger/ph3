package de.zebrajaeger.phserver.hardware;

import java.util.Optional;

public interface HardwareService {
    PanoHead getPanoHead();

    Optional<PowerGauge> getPowerGauge();

    Optional<AccelerationSensor> getAccelerationSensor();
    SystemDevice getSystemDevice();
}

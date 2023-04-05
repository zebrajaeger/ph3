package de.zebrajaeger.phserver.hardware;

import java.util.Optional;

public interface HardwareService {
    Optional<PanoHead> getPanoHead();

    Optional<PowerGauge> getPowerGauge();

    Optional<AccelerationSensor> getAccelerationSensor();
    Optional<SystemDevice> getSystemDevice();
}

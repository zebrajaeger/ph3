package de.zebrajaeger.phserver.hardware;

public interface HardwareService {
    PanoHead getPanoHead();

    PowerGauge getPowerGauge();

    AccelerationSensor getAccelerationSensor();
}

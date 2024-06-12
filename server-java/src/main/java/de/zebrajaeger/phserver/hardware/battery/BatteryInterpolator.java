package de.zebrajaeger.phserver.hardware.battery;

public interface BatteryInterpolator {
    double getPercentForVoltage(double voltage);
}

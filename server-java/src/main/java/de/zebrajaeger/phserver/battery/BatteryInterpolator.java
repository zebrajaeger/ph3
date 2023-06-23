package de.zebrajaeger.phserver.battery;

public interface BatteryInterpolator {
    double getPercentForVoltage(double voltage);
}

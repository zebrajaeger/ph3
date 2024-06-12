package de.zebrajaeger.phserver.hardware.battery;

public record VoltagePercent(double voltage, double percent) implements Comparable<VoltagePercent> {
    @Override
    public int compareTo(VoltagePercent o) {
        double x = voltage - o.voltage;
        if (x == 0) {
            return 0;
        }
        return voltage - o.voltage < 0 ? -1 : 1;
    }
}

package de.zebrajaeger.phserver.battery;

import java.util.TreeSet;
import java.util.function.Function;

class BatteryValues {
    private final TreeSet<VoltagePercent> values;

    public BatteryValues() {
        this(new TreeSet<>());
    }

    public BatteryValues(TreeSet<VoltagePercent> values) {
        this.values = values;
    }

    public BatteryValues createBatteryPackWithCellCountOf(int cellCount) {
        return new BatteryValues(new TreeSet<>(values
                .stream()
                .map(v -> new VoltagePercent(v.voltage() * cellCount, v.percent()))
                .toList()));
    }

    public void add(VoltagePercent value) {
        values.add(value);
    }

    public void add(double voltage, double percent) {
        add(new VoltagePercent(voltage, percent));
    }

    public double getMinVoltage() {
        return values.first().voltage();
    }

    public double getMaxVoltage() {
        return values.last().voltage();
    }

    public double[] getVoltageArray() {
        return toArray(VoltagePercent::voltage);
    }

    public double[] getPercentArray() {
        return toArray(VoltagePercent::percent);
    }

    private double[] toArray(Function<VoltagePercent, Double> f) {
        double[] result = new double[values.size()];
        int i = 0;
        for (VoltagePercent v : values) {
            result[i++] = f.apply(v);
        }
        return result;
    }
}

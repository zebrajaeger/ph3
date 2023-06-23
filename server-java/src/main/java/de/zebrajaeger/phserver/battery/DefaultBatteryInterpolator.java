package de.zebrajaeger.phserver.battery;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;

public class DefaultBatteryInterpolator implements BatteryInterpolator {
    final UnivariateFunction function;
    final double min;
    final double max;

    public DefaultBatteryInterpolator(BatteryValues data) {
        final UnivariateInterpolator interpolator = new LinearInterpolator();
        function = interpolator.interpolate(data.getVoltageArray(), data.getPercentArray());
        min = data.getMinVoltage();
        max = data.getMaxVoltage();
    }

    public double getPercentForVoltage(double voltage) {
        return function.value(Math.min(max, Math.max(min, voltage)));
    }
}

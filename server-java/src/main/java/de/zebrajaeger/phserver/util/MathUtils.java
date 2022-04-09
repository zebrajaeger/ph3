package de.zebrajaeger.phserver.util;

public class MathUtils {
    private MathUtils() {
    }

    public static float map(float rawValue, float rawMinValue, float rawMaxValue, float rangeMinValue, float rangeMaxValue) {
        return (rawValue - rawMinValue) * (rangeMaxValue - rangeMinValue) / (rawMaxValue - rawMinValue) + rangeMinValue;
    }

    public static double map(double rawValue, double rawMinValue, double rawMaxValue, double rangeMinValue, double rangeMaxValue) {
        return (rawValue - rawMinValue) * (rangeMaxValue - rangeMinValue) / (rawMaxValue - rawMinValue) + rangeMinValue;
    }
}

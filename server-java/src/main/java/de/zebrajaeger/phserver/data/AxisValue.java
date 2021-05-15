package de.zebrajaeger.phserver.data;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class AxisValue {
    private int axisIndex;
    private int value;

    public AxisValue(int axisIndex, int value) {
        this.axisIndex = axisIndex;
        this.value = value;
    }

    public int getAxisIndex() {
        return axisIndex;
    }

    public void setAxisIndex(int axisIndex) {
        this.axisIndex = axisIndex;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}

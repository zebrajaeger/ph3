package de.zebrajaeger.phserver.data;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Power {
    private final Double voltage;
    private final Double current;
    private final Double power;

    public Power(Double voltage, Double current) {
        this.voltage = voltage;
        this.current = current;
        this.power = voltage * current;
    }

    public Power(Double power) {
        this.voltage = null;
        this.current = null;
        this.power = power;
    }

    //<editor-fold desc="boilerplate">
    public Double getVoltage() {
        return voltage;
    }

    public Double getCurrent() {
        return current;
    }

    public Double getPower() {
        return power;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    //</editor-fold>
}

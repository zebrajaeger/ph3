package de.zebrajaeger.phserver.settings;

import de.zebrajaeger.phserver.data.Range;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class RangeSettings {
    private Double from;
    private Double to;

    public RangeSettings() {
    }

    public RangeSettings(Double from, Double to) {
        this.from = from;
        this.to = to;
    }

    public void getAll(Range range) {
        range.setFrom(from);
        range.setTo(to);
    }

    public void setAll(Range range) {
        this.from = range.getFrom();
        this.to = range.getTo();
    }

    //<editor-fold desc="boilerplate">
    public Double getFrom() {
        return from;
    }

    public void setFrom(Double from) {
        this.from = from;
    }

    public Double getTo() {
        return to;
    }

    public void setTo(Double to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    //</editor-fold>
}

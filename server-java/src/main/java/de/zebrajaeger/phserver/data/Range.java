package de.zebrajaeger.phserver.data;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Optional;

public class Range {
    private Double from;
    private Double to;

    public Range() {
    }

    public Range(Range range) {
        this.from = range.from;
        this.to = range.to;
    }

    public Range(Double from, Double to) {
        this.from = from;
        this.to = to;
    }

    public boolean isComplete() {
        return from != null && to != null;
    }

    public Optional<Double> getSize() {
        return isComplete() ? Optional.of(to - from) : Optional.empty();
    }

    public Range createNormalized() {
        return (isComplete() && from > to) ? new Range(to, from) : new Range(from, to);
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

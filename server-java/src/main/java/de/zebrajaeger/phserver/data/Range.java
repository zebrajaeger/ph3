package de.zebrajaeger.phserver.data;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class Range {
    private double from = 0;
    private double to = 0;

    public Range() {
    }

    public Range(Range range) {
        this.from = range.from;
        this.to = range.to;
    }

    public Range(double from, double to) {
        this.from = from;
        this.to = to;
    }

    public double getFrom() {
        return from;
    }

    public void setFrom(double from) {
        this.from = from;
    }

    public double getTo() {
        return to;
    }

    public void setTo(double to) {
        this.to = to;
    }

    public double getSize() {
        return to - from;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}

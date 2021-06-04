package de.zebrajaeger.phserver.data;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class Pano {

    private FieldOfViewPartial fieldOfViewPartial;
    private double horizontalMinimumOverlap = 0.25d;
    private double verticalMinimumOverlap = 0.25d;

    public Pano() {
    }

    public Pano(Pano pano) {
        this.fieldOfViewPartial = new FieldOfViewPartial(pano.fieldOfViewPartial);
        this.horizontalMinimumOverlap = pano.horizontalMinimumOverlap;
        this.verticalMinimumOverlap = pano.verticalMinimumOverlap;
    }

    public Pano(FieldOfViewPartial fieldOfViewPartial, double horizontalMinimumOverlap, double verticalMinimumOverlap) {
        this.fieldOfViewPartial = fieldOfViewPartial;
        this.horizontalMinimumOverlap = horizontalMinimumOverlap;
        this.verticalMinimumOverlap = verticalMinimumOverlap;
    }

    public FieldOfViewPartial getFieldOfViewPartial() {
        return fieldOfViewPartial;
    }

    public void setFieldOfViewPartial(FieldOfViewPartial fieldOfViewPartial) {
        this.fieldOfViewPartial = fieldOfViewPartial;
    }

    public double getHorizontalMinimumOverlap() {
        return horizontalMinimumOverlap;
    }

    public void setHorizontalMinimumOverlap(double horizontalMinimumOverlap) {
        this.horizontalMinimumOverlap = horizontalMinimumOverlap;
    }

    public double getVerticalMinimumOverlap() {
        return verticalMinimumOverlap;
    }

    public void setVerticalMinimumOverlap(double verticalMinimumOverlap) {
        this.verticalMinimumOverlap = verticalMinimumOverlap;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}

package de.zebrajaeger.phserver.data;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class Pano {

    private FieldOfView fieldOfView;
    private double horizontalMinimumOverlap = 0.25d;
    private double verticalMinimumOverlap = 0.25d;

    public Pano() {
    }

    public Pano(Pano pano) {
        this.fieldOfView = new FieldOfView(pano.fieldOfView);
        this.horizontalMinimumOverlap = pano.horizontalMinimumOverlap;
        this.verticalMinimumOverlap = pano.verticalMinimumOverlap;
    }

    public Pano(FieldOfView fieldOfView, double horizontalMinimumOverlap, double verticalMinimumOverlap) {
        this.fieldOfView = fieldOfView;
        this.horizontalMinimumOverlap = horizontalMinimumOverlap;
        this.verticalMinimumOverlap = verticalMinimumOverlap;
    }

    public FieldOfView getFieldOfView() {
        return fieldOfView;
    }

    public void setFieldOfView(FieldOfView fieldOfView) {
        this.fieldOfView = fieldOfView;
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

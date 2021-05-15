package de.zebrajaeger.phserver.data;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class FieldOfView {
    private Range horizontal;
    private Range vertical;

    public FieldOfView() {
    }

    public FieldOfView(FieldOfView fov) {
        if (fov.horizontal != null) {
            this.horizontal = new Range(fov.horizontal);
        }
        if (fov.vertical != null) {
            this.vertical = new Range(fov.vertical);
        }
    }

    public FieldOfView(Range horizontal, Range vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public FieldOfView(double hFrom, double hTo, double vFrom, double vTo) {
        this.horizontal = new Range(hFrom, hTo);
        this.vertical = new Range(vFrom, vTo);
    }

    public Range getHorizontal() {
        return horizontal;
    }

    public void setHorizontal(Range horizontal) {
        this.horizontal = horizontal;
    }

    public Range getVertical() {
        return vertical;
    }

    public void setVertical(Range vertical) {
        this.vertical = vertical;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}

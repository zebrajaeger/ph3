package de.zebrajaeger.phserver.data;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FieldOfView {
    private Range horizontal = new Range();
    private Range vertical = new Range();

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

    public FieldOfView(double hFrom, double hTo, double vFrom, double vTo) {
        this.horizontal = new Range(hFrom, hTo);
        this.vertical = new Range(vFrom, vTo);
    }

    //<editor-fold desc="boilerplate">
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
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    //</editor-fold>
}

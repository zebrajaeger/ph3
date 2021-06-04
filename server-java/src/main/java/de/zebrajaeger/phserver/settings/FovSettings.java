package de.zebrajaeger.phserver.settings;

import de.zebrajaeger.phserver.data.FieldOfView;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FovSettings {
    private RangeSettings horizontal = new RangeSettings();
    private RangeSettings vertical = new RangeSettings();

    public void setAll(FieldOfView fov) {
        horizontal.setAll(fov.getHorizontal());
        vertical.setAll(fov.getVertical());
    }

    public void getAll(FieldOfView fov) {
        horizontal.getAll(fov.getHorizontal());
        vertical.getAll(fov.getVertical());
    }

    //<editor-fold desc="boilerplate">
    public RangeSettings getHorizontal() {
        return horizontal;
    }

    public void setHorizontal(RangeSettings horizontal) {
        this.horizontal = horizontal;
    }

    public RangeSettings getVertical() {
        return vertical;
    }

    public void setVertical(RangeSettings vertical) {
        this.vertical = vertical;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    //</editor-fold>
}

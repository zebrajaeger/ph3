package de.zebrajaeger.phserver.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.zebrajaeger.phserver.data.FieldOfView;
import de.zebrajaeger.phserver.data.FieldOfViewPartial;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FovSettings {
    private RangeSettings horizontal = new RangeSettings();
    private RangeSettings vertical = new RangeSettings();
    private boolean partial = true;

    @JsonIgnore
    public void setAll(FieldOfView fov) {
        horizontal.setAll(fov.getHorizontal());
        vertical.setAll(fov.getVertical());
        partial = true;
    }

    @JsonIgnore
    public void setAll(FieldOfViewPartial fov) {
        horizontal.setAll(fov.getHorizontal());
        vertical.setAll(fov.getVertical());
        partial = fov.isPartial();
    }

    @JsonIgnore
    public void getAll(FieldOfView fov) {
        horizontal.getAll(fov.getHorizontal());
        vertical.getAll(fov.getVertical());
    }

    @JsonIgnore
    public void getAll(FieldOfViewPartial fov) {
        horizontal.getAll(fov.getHorizontal());
        vertical.getAll(fov.getVertical());
        fov.setPartial(partial);
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

    public boolean isPartial() {
        return partial;
    }

    public void setPartial(boolean partial) {
        this.partial = partial;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    //</editor-fold>
}

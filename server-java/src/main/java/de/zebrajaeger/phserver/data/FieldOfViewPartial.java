package de.zebrajaeger.phserver.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FieldOfViewPartial extends FieldOfView {

    private boolean partial = false;

    public FieldOfViewPartial() {
    }

    public FieldOfViewPartial(FieldOfViewPartial fov) {
        super(fov);
        this.partial = fov.partial;
    }

    public FieldOfViewPartial(Range horizontal, Range vertical, boolean partial) {
        super(horizontal, vertical);
        this.partial = partial;
    }

    public FieldOfViewPartial normalize() {
        return new FieldOfViewPartial(
                getHorizontal() == null ? null : getHorizontal().normalize(),
                getVertical() == null ? null : getVertical().normalize(),
                partial);
    }

    @Override
    public boolean isComplete() {
        return partial
                ? super.isComplete()
                : getVertical().isComplete();
    }
}

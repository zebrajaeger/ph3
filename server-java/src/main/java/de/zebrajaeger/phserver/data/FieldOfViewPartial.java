package de.zebrajaeger.phserver.data;

public class FieldOfViewPartial extends FieldOfView {

    private boolean partial = false;

    public FieldOfViewPartial() {
    }

    public FieldOfViewPartial(double hFrom, double hTo, double vFrom, double vTo, boolean partial) {
        super(hFrom, hTo, vFrom, vTo);
        this.partial = partial;
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

    public boolean isPartial() {
        return partial;
    }

    public void setPartial(boolean partial) {
        this.partial = partial;
    }
}

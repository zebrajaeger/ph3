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

    public boolean isPartial() {
        return partial;
    }

    public void setPartial(boolean partial) {
        this.partial = partial;
    }
}

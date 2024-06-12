package de.zebrajaeger.phserver.hardware.axis;

public interface Axis {
    void setVelocity(double velocity) throws Exception;
    boolean moveTo(double posDeg) throws Exception;
    void moveRelative(double angleDeg) throws Exception;

    double getDegValue();
    void setRawValue(int pos);
    int getRawValue();

    void normalizeAxisPosition();
    void setToZero() throws Exception;
    void adaptOffset() throws Exception;
}

package de.zebrajaeger.phserver.hardware.axis;

import de.zebrajaeger.phserver.data.AxisIndex;

public interface Axis {
    AxisIndex getAxisIndex();

    void setVelocity(double velocity) throws Exception;

    boolean moveTo(double posDeg) throws Exception;

    void moveRelative(double angleDeg) throws Exception;

    void setTargetRawValue(int pos);
    int getTargetRawValue();
    double getTargetDegValue();

    void setMeasuredRawValue(int pos);
    int getMeasuredRawValue();
    double getMeasuredDegValue();

    void normalizeAxisPosition();

    void setToZero() throws Exception;

    void adaptOffset() throws Exception;
}

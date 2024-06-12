package de.zebrajaeger.phserver.hardware.axis;

import de.zebrajaeger.phserver.data.AxisIndex;
import de.zebrajaeger.phserver.hardware.Actor;
import de.zebrajaeger.phserver.translation.AxisParameters;
import lombok.Getter;
import lombok.Setter;


public class SimpleAxis implements Axis {
    private final Actor actor;
    private final AxisIndex axisIndex;
    private final AxisParameters axisParameters;

    @Getter
    @Setter
    private int rawValue = 0;

    public SimpleAxis(Actor actor, AxisIndex axisIndex, AxisParameters axisParameters) {
        this.actor = actor;
        this.axisIndex = axisIndex;
        this.axisParameters = axisParameters;
    }

    @Override
    public void setVelocity(double velocity) throws Exception {
        actor.setTargetVelocity(axisIndex, (int) (velocity * axisParameters.getMaxStepFrequency()));
    }

    @Override
    public boolean moveTo(double posDeg) throws Exception {
        if (axisParameters.isInverted()) {
            posDeg = -posDeg;
        }
        int targetPos = axisParameters.degToRaw(posDeg);
        if (rawValue != targetPos) {
            setTargetPosRaw(targetPos);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void moveRelative(double angleDeg) throws Exception {
        if (angleDeg == 0d) {
            return;
        }

        if (axisParameters.isInverted()) {
            angleDeg = -angleDeg;
        }

        setTargetPosRaw(rawValue + axisParameters.degToRaw(angleDeg));
    }

    @Override
    public double getDegValue() {
        return axisParameters.isInverted()
                ? -(axisParameters.rawToDeg(rawValue))
                : axisParameters.rawToDeg(rawValue);
    }


    @Override
    public void normalizeAxisPosition() {
        // ignore
    }

    @Override
    public void setToZero() throws Exception {
        // ignore
    }

    @Override
    public void adaptOffset() throws Exception {
        // ignore
    }

    private void setTargetPosRaw(int pos) throws Exception {
        actor.setTargetPos(axisIndex, pos);
    }
}

package de.zebrajaeger.phserver.hardware.axis;

import de.zebrajaeger.phserver.data.AxisIndex;
import de.zebrajaeger.phserver.event.AxisChangedEvent;
import de.zebrajaeger.phserver.hardware.actor.Actor;
import de.zebrajaeger.phserver.translation.AxisParameters;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

@AllArgsConstructor
@Getter
@Setter
@Slf4j
public class AxisWithOffset implements Axis {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final Actor actor;
    private final AxisIndex axisIndex;
    private final AxisParameters axisParameters;

    private int offsetRaw = 0;
    private int targetRawValue = 0;
    private int measuredRawValue = 0;

    private boolean targetInitialized = false;

    public AxisWithOffset(ApplicationEventPublisher applicationEventPublisher, Actor actor, AxisIndex axisIndex, AxisParameters axisParameters) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.actor = actor;
        this.axisIndex = axisIndex;
        this.axisParameters = axisParameters;

        try {
            actor.setLimit(
                    axisIndex,
                    6 * 10,
                    axisParameters.getMaxStepFrequency(),
                    axisParameters.getMaxAccelerationFrequency());
        } catch (Exception e) {
            log.error("could not set limit for axisIndex: {}", axisIndex, e);
        }
    }

    /**
     * @param posDeg Position to go to.
     * @return true: already at the required position; false: move required;
     */
    public boolean moveTo(double posDeg) throws Exception {
        if (getAxisParameters().isInverted()) {
            posDeg = -posDeg;
        }
        int targetPositionSteps = getAxisParameters().degToRaw(posDeg);
        final int targetPosStepsWithoutOffset = targetPositionSteps - getOffsetRaw();
        if (getTargetRawValue() != targetPosStepsWithoutOffset) {
            setTargetRawValue(targetPosStepsWithoutOffset);
            return false;
        } else {
            return true;
        }
    }

    public void moveRelative(double angleDeg) throws Exception {
        if (angleDeg == 0d) {
            return;
        }

        if (getAxisParameters().isInverted()) {
            angleDeg = -angleDeg;
        }
        setTargetRawValue(getTargetRawValue() + getAxisParameters().degToRaw(angleDeg));
    }

    public void adaptOffset() throws Exception {
        if (getTargetRawValue() != 0) {
            offsetRaw += getTargetRawValue();
            getActor().setActualAndTargetPos(getAxisIndex(), 0);
        }
    }

    public void normalizeAxisPosition() {
        double a = getAxisParameters().rawToDeg(getTargetRawValue() + getOffsetRaw());
        int revolutions = (int) (a / 360d);
        int rawDelta = getAxisParameters().degToRaw(360 * revolutions);
        offsetRaw -= rawDelta;
    }

    public void setToZero() throws Exception {
        offsetRaw = 0;
        targetRawValue = 0;
        getActor().setActualAndTargetPos(getAxisIndex(), 0);
        sendChangeEvent();
    }

    /**
     * @param velocity [0..1]
     */
    public void setVelocity(double velocity) throws Exception {
        if (velocity < 0 || velocity > 1) {
            throw new IllegalArgumentException("Velocity value must [0..1]. But is " + velocity);
        }
        getActor().setTargetVelocity(getAxisIndex(), (int) (velocity * getAxisParameters().getMaxStepFrequency()));
    }

    public double getTargetDegValue() {
        return getAxisParameters().isInverted()
                ? -(getAxisParameters().rawToDeg(getTargetRawValue() + getOffsetRaw()))
                : getAxisParameters().rawToDeg(getTargetRawValue() + getOffsetRaw());
    }

    public double getMeasuredDegValue() {
        return getAxisParameters().isInverted()
                ? -(getAxisParameters().rawToDeg(getMeasuredRawValue() + getOffsetRaw()))
                : getAxisParameters().rawToDeg(getMeasuredRawValue() + getOffsetRaw());
    }

    public void setTargetRawValue(int pos) {
        targetRawValue = pos;
        try {
            getActor().setTargetPos(getAxisIndex(), pos);
        } catch (Exception e) {
            log.error("Could not send target position request to actor");
        }
        sendChangeEvent();
    }

    public void setMeasuredRawValue(int measuredRawValue) {
        this.measuredRawValue = measuredRawValue;
        if (!isTargetInitialized()) {
            setTargetInitialized(true);
            targetRawValue = measuredRawValue;
        }
        sendChangeEvent();
    }

    private void sendChangeEvent() {
        getApplicationEventPublisher().publishEvent(new AxisChangedEvent(this));
    }
}

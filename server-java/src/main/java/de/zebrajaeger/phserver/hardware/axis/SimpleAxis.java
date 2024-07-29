package de.zebrajaeger.phserver.hardware.axis;

import de.zebrajaeger.phserver.data.AxisIndex;
import de.zebrajaeger.phserver.event.ActorStatusEvent;
import de.zebrajaeger.phserver.event.AxisChangedEvent;
import de.zebrajaeger.phserver.hardware.actor.Actor;
import de.zebrajaeger.phserver.translation.AxisParameters;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEventPublisher;


@Getter
@Setter
public class SimpleAxis implements Axis {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final Actor actor;
    private final AxisIndex axisIndex;
    private final AxisParameters axisParameters;

    private int targetRawValue = 0;
    private int measuredRawValue = 0;

    private boolean targetInitialized = false;

    public SimpleAxis(ApplicationEventPublisher applicationEventPublisher, Actor actor, AxisIndex axisIndex, AxisParameters axisParameters) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.actor = actor;
        this.axisIndex = axisIndex;
        this.axisParameters = axisParameters;
    }

    @Override
    public void setVelocity(double velocity) throws Exception {
        getActor().setTargetVelocity(getAxisIndex(), (int) (velocity * getAxisParameters().getMaxStepFrequency()));
    }

    @Override
    public boolean moveTo(double posDeg) throws Exception {
        if (getAxisParameters().isInverted()) {
            posDeg = -posDeg;
        }
        int targetPos = getAxisParameters().degToRaw(posDeg);
        if (getTargetRawValue() != targetPos) {
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

        if (getAxisParameters().isInverted()) {
            angleDeg = -angleDeg;
        }

        setTargetPosRaw(getTargetRawValue() + getAxisParameters().degToRaw(angleDeg));
    }

    @Override
    public double getTargetDegValue() {
        return getAxisParameters().isInverted()
                ? -(getAxisParameters().rawToDeg(getTargetRawValue()))
                : getAxisParameters().rawToDeg(getTargetRawValue());
    }

    @Override
    public double getMeasuredDegValue() {
        return getAxisParameters().isInverted()
                ? -(getAxisParameters().rawToDeg(getMeasuredRawValue()))
                : getAxisParameters().rawToDeg(getMeasuredRawValue());
    }

    @Override
    public void normalizeAxisPosition() {
        // ignore
    }

    @Override
    public void setToZero() throws Exception {
        setTargetRawValue(0);
        getActor().resetPos();
    }

    @Override
    public void adaptOffset() throws Exception {
        // ignore
    }

    private void setTargetPosRaw(int pos) throws Exception {
        sendChangeEvent();
        getActor().setTargetPos(getAxisIndex(), pos);
    }

    public void setTargetRawValue(int targetRawValue) {
        this.targetRawValue = targetRawValue;
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

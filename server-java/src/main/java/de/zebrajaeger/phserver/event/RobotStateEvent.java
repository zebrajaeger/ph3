package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.data.RobotState;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class RobotStateEvent {
    private final RobotState robotState;
    private Exception error;

    public RobotStateEvent(RobotState robotState) {
        this.robotState = robotState;
    }

    public RobotStateEvent(RobotState robotState, Exception error) {
        this.robotState = robotState;
        this.error = error;
    }

    public RobotState getRobotState() {
        return robotState;
    }

    public Exception getError() {
        return error;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.data.RobotState;

public record RobotStateEvent(RobotState robotState, Exception error) {
}

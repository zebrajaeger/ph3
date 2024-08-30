package de.zebrajaeger.phserver.hardware.udp;

import java.util.Arrays;
import java.util.Optional;

public enum CommandType {
    MOVE_X(1),
    MOVE_Y(2),
    MOVE_XY(3),

    MOVE_TO_X(4),
    MOVE_TO_Y(5),
    MOVE_TO_XY(6),

    SPEED_X(7),
    SPEED_Y(8),
    SPEED_XY(9),

    STOP_X(32),
    STOP_Y(33),
    STOP_XY(34),

    FORCE_STOP(48),

    SET_POS_X(64),
    SET_POS_Y(65),
    SET_POS_XY(66),

    PING(250),
    PONG(251),

    ERROR(254),
    UNKNOWN(255);

    private final int value;

    public static Optional<CommandType> valueOf(int value) {
        return Arrays.stream(values())
                .filter(legNo -> legNo.value == value)
                .findFirst();
    }

    CommandType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

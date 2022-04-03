package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.data.Position;

public class JoystickPositionEvent {
    private Position position;

    public JoystickPositionEvent(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }
}

package de.zebrajaeger.phserver.pano;

import de.zebrajaeger.phserver.data.Position;

public class GoToPosCommand extends Command {
    private final Position position;

    public GoToPosCommand(String description, Position position) {
        super(description);
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }
}

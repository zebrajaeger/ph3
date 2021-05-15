package de.zebrajaeger.phserver.pano;

import de.zebrajaeger.phserver.data.Position;

public class GoToPosCommand extends Command {
    private Position position;

    public GoToPosCommand(int index, String description, Position position) {
        super(index, description);
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }
}

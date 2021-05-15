package de.zebrajaeger.phserver.pano;

import de.zebrajaeger.phserver.data.Shot;

public class TakeShotCommand extends Command {
    private Shot shot;

    public TakeShotCommand(int index, String description, Shot shot) {
        super(index, description);
        this.shot = shot;
    }

    public Shot getShot() {
        return shot;
    }
}

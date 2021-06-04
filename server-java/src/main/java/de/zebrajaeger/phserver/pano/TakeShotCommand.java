package de.zebrajaeger.phserver.pano;

import de.zebrajaeger.phserver.data.Shot;

public class TakeShotCommand extends Command {
    private final Shot shot;

    public TakeShotCommand(String description, Shot shot) {
        super(description);
        this.shot = shot;
    }

    public Shot getShot() {
        return shot;
    }
}

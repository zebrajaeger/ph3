package de.zebrajaeger.phserver.pano;

public class WaitCommand extends Command {
    private int timeMs;

    public WaitCommand(int index, String description, int timeMs) {
        super(index, description);
        this.timeMs = timeMs;
    }

    public int getTimeMs() {
        return timeMs;
    }
}

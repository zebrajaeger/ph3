package de.zebrajaeger.phserver.pano;

public class WaitCommand extends Command {
    private final int timeMs;

    public WaitCommand(String description, int timeMs) {
        super(description);
        this.timeMs = timeMs;
    }

    public int getTimeMs() {
        return timeMs;
    }
}

package de.zebrajaeger.phserver.event;

public class JoggingEvent {
    private boolean jogging;

    public JoggingEvent(boolean jogging) {
        this.jogging = jogging;
    }

    public boolean isJogging() {
        return jogging;
    }
}

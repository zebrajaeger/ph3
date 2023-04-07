package de.zebrajaeger.phserver.event;

public class JoggingChangedEvent {
    private final boolean jogging;

    public JoggingChangedEvent(boolean jogging) {
        this.jogging = jogging;
    }

    public boolean isJogging() {
        return jogging;
    }
}

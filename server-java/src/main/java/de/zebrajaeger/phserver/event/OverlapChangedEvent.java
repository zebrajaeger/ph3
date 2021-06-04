package de.zebrajaeger.phserver.event;

public class OverlapChangedEvent {
    private final double minimumOverlapH;
    private final double minimumOverlapV;

    public OverlapChangedEvent(double minimumOverlapH, double minimumOverlapV) {
        this.minimumOverlapH = minimumOverlapH;
        this.minimumOverlapV = minimumOverlapV;
    }

    public double getMinimumOverlapH() {
        return minimumOverlapH;
    }

    public double getMinimumOverlapV() {
        return minimumOverlapV;
    }
}

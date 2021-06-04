package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.data.CalculatedPano;

public class CalculatedPanoChangedEvent {
    private final CalculatedPano calculatedPano;

    public CalculatedPanoChangedEvent(CalculatedPano calculatedPano) {
        this.calculatedPano = calculatedPano;
    }

    public CalculatedPano getCalculatedPano() {
        return calculatedPano;
    }
}

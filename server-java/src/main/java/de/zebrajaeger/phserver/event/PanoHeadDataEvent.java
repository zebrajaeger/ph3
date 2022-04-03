package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.data.PanoHeadData;

public class PanoHeadDataEvent {
    private PanoHeadData data;

    public PanoHeadDataEvent(PanoHeadData data) {
        this.data = data;
    }

    public PanoHeadData getData() {
        return data;
    }
}

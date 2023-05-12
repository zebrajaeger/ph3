package de.zebrajaeger.phserver.hardware;

import de.zebrajaeger.phserver.data.PanoHeadData;

import java.io.IOException;

public interface PanoHead {
    PanoHeadData read() throws IOException;

    void startFocus(int focusTimeMs) throws IOException;

    void startTrigger(int triggerTimeMs) throws IOException;

    void startShot(int focusTimeMs, int triggerTimeMs) throws IOException;

    void setLimit(int axisIndex, int velocityMinHz, int velocityMaxHz, int accelerationMaxHzPerSecond) throws IOException;

    void setTargetVelocity(int axis, int velocity) throws IOException;

    void setTargetPos(int axis, int pos) throws IOException;

    void stopAll() throws IOException;

    void setActualPos(int axisIndex, int pos) throws IOException;

    void setActualAndTargetPos(int axisIndex, int pos) throws IOException;

    void resetPos() throws IOException;
}

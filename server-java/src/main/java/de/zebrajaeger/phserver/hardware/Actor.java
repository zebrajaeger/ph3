package de.zebrajaeger.phserver.hardware;

import de.zebrajaeger.phserver.data.AxisIndex;

public interface Actor {
    void startFocus(int focusTimeMs) throws Exception;

    void startTrigger(int triggerTimeMs) throws Exception;

    void startShot(int focusTimeMs, int triggerTimeMs) throws Exception;

    void setLimit(AxisIndex axisIndex, int velocityMinHz, int velocityMaxHz, int accelerationMaxHzPerSecond) throws Exception;

    void setTargetVelocity(AxisIndex axisIndex, int velocity) throws Exception;

    void setTargetPos(AxisIndex axisIndex, int pos) throws Exception;

    void stopAll() throws Exception;

    // --- For Offset

//    void setActualPos(AxisIndex axisIndex, int pos) throws Exception;
//
    void setActualAndTargetPos(AxisIndex axisIndex, int pos) throws Exception;
//
//    void resetPos() throws Exception;
}
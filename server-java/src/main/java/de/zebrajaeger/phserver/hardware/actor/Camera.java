package de.zebrajaeger.phserver.hardware.actor;

public interface Camera {
    void startFocus(int focusTimeMs) throws Exception;

    void startTrigger(int triggerTimeMs) throws Exception;

    void startShot(int focusTimeMs, int triggerTimeMs) throws Exception;
}

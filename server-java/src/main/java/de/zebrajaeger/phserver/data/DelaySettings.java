package de.zebrajaeger.phserver.data;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class DelaySettings {
    private int waitAfterMove = 0;
    private int waitAfterShot = 0;
    private int waitBetweenShots = 0;

    public DelaySettings() {
    }

    public DelaySettings(int waitAfterMove, int waitAfterShot, int waitBetweenShots) {
        this.waitAfterMove = waitAfterMove;
        this.waitAfterShot = waitAfterShot;
        this.waitBetweenShots = waitBetweenShots;
    }

    public int getWaitAfterMove() {
        return waitAfterMove;
    }

    public void setWaitAfterMove(int waitAfterMove) {
        this.waitAfterMove = waitAfterMove;
    }

    public int getWaitAfterShot() {
        return waitAfterShot;
    }

    public void setWaitAfterShot(int waitAfterShot) {
        this.waitAfterShot = waitAfterShot;
    }

    public int getWaitBetweenShots() {
        return waitBetweenShots;
    }

    public void setWaitBetweenShots(int waitBetweenShots) {
        this.waitBetweenShots = waitBetweenShots;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}

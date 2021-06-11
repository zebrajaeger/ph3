package de.zebrajaeger.phserver.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.zebrajaeger.phserver.data.Delay;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class DelaySettings {
    private int waitAfterMove = 1000;
    private int waitAfterShot = 0;
    private int waitBetweenShots = 0;

    public DelaySettings() {
    }

    @JsonIgnore
    public void setAll(Delay delay) {
        waitAfterMove = delay.getWaitAfterMove();
        waitAfterShot = delay.getWaitAfterShot();
        waitBetweenShots = delay.getWaitBetweenShots();
    }

    @JsonIgnore
    public Delay getAll(Delay delay) {
        delay.setWaitAfterMove(waitAfterMove);
        delay.setWaitAfterShot(waitAfterShot);
        delay.setWaitBetweenShots(waitBetweenShots);
        return delay;
    }

    //<editor-fold desc="boilerplate">
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
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    //</editor-fold>
}

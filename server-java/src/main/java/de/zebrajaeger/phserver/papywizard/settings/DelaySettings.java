package de.zebrajaeger.phserver.papywizard.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.zebrajaeger.phserver.data.Delay;
import lombok.Data;

@Data
public class DelaySettings {
    private int waitAfterMove = 1000;
    private int waitAfterShot = 0;
    private int waitBetweenShots = 0;

    @JsonIgnore
    public void setAll(Delay delay) {
        waitAfterMove = delay.getWaitAfterMove();
        waitAfterShot = delay.getWaitAfterShot();
        waitBetweenShots = delay.getWaitBetweenShots();
    }

    @SuppressWarnings("UnusedReturnValue")
    @JsonIgnore
    public Delay getAll(Delay delay) {
        delay.setWaitAfterMove(waitAfterMove);
        delay.setWaitAfterShot(waitAfterShot);
        delay.setWaitBetweenShots(waitBetweenShots);
        return delay;
    }
}

package de.zebrajaeger.phserver.settings;

import lombok.Data;

@Data
public class DelaySettings implements SettingsValue<DelaySettings> {
    private int waitAfterMove = 1000;
    private int waitAfterShot = 0;
    private int waitBetweenShots = 0;

    @Override
    public void read(DelaySettings value) {
        value.setWaitAfterMove(waitAfterMove);
        value.setWaitAfterShot(waitAfterShot);
        value.setWaitBetweenShots(waitBetweenShots);
    }

    @Override
    public void write(DelaySettings value) {
        waitAfterMove = value.getWaitAfterMove();
        waitAfterShot = value.getWaitAfterShot();
        waitBetweenShots = value.getWaitBetweenShots();
    }
}

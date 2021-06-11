package de.zebrajaeger.phserver.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.zebrajaeger.phserver.data.Shot;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ShotSetting {
    private int focusTimeMs = 1000;
    private int triggerTimeMs = 1000;

    public ShotSetting() {
    }

    public ShotSetting(Shot shot) {
        setAll(shot);
    }

    public ShotSetting(int focusTimeMs, int triggerTimeMs) {
        this.focusTimeMs = focusTimeMs;
        this.triggerTimeMs = triggerTimeMs;
    }

    @JsonIgnore
    public Shot getAll(Shot shot) {
        shot.setFocusTimeMs(this.focusTimeMs);
        shot.setTriggerTimeMs(this.triggerTimeMs);
        return shot;
    }

    @JsonIgnore
    public void setAll(Shot shot) {
        this.focusTimeMs = shot.getFocusTimeMs();
        this.triggerTimeMs = shot.getTriggerTimeMs();
    }

    public int getFocusTimeMs() {
        return focusTimeMs;
    }

    public void setFocusTimeMs(int focusTimeMs) {
        this.focusTimeMs = focusTimeMs;
    }

    public int getTriggerTimeMs() {
        return triggerTimeMs;
    }

    public void setTriggerTimeMs(int triggerTimeMs) {
        this.triggerTimeMs = triggerTimeMs;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}

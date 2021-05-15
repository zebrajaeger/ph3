package de.zebrajaeger.phserver.data;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Shot {
    private int focusTimeMs;
    private int triggerTimeMs;

    public Shot(int focusTimeMs, int triggerTimeMs) {
        this.focusTimeMs = focusTimeMs;
        this.triggerTimeMs = triggerTimeMs;
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

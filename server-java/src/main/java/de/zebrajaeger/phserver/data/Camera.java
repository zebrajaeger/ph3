package de.zebrajaeger.phserver.data;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class Camera {
    protected boolean focus;
    protected boolean trigger;

    public boolean isFocus() {
        return focus;
    }

    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    public boolean isTrigger() {
        return trigger;
    }

    public void setTrigger(boolean trigger) {
        this.trigger = trigger;
    }

    public boolean isActive() {
        return focus || trigger;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}

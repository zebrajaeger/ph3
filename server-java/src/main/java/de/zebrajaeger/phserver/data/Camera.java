package de.zebrajaeger.phserver.data;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

public class Camera {
    protected boolean focus;
    protected boolean trigger;

    public Camera() {
    }

    public Camera(boolean focus, boolean trigger) {
        this.focus = focus;
        this.trigger = trigger;
    }

    public Camera(Camera other) {
        this.focus = other.focus;
        this.trigger = other.trigger;
    }

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Camera camera = (Camera) o;
        return focus == camera.focus && trigger == camera.trigger;
    }

    @Override
    public int hashCode() {
        return Objects.hash(focus, trigger);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

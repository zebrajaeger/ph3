package de.zebrajaeger.phserver.data;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Actor {
    protected ActorAxis x = new ActorAxis();
    protected ActorAxis y = new ActorAxis();

    public ActorAxis getX() {
        return x;
    }

    public void setX(ActorAxis x) {
        this.x = x;
    }

    public ActorAxis getY() {
        return y;
    }

    public void setY(ActorAxis y) {
        this.y = y;
    }

    public boolean isActive() {
        return x.isMoving() || y.isMoving();
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}

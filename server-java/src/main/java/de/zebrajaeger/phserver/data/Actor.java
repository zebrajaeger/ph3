package de.zebrajaeger.phserver.data;

import lombok.Data;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
@Data
public class Actor {
    protected ActorAxis x = new ActorAxis();
    protected ActorAxis y = new ActorAxis();

    public boolean isActive() {
        return x.isMoving() || y.isMoving();
    }
}

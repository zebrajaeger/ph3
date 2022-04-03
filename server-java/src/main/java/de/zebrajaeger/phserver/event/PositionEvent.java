package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.data.Position;
import de.zebrajaeger.phserver.data.RawPosition;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PositionEvent {
    private final RawPosition rawPosition;
    private final Position position;

    public PositionEvent(RawPosition rawPosition, Position position) {
        this.rawPosition = rawPosition;
        this.position = position;
    }

    public RawPosition getRawPosition() {
        return rawPosition;
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

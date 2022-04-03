package de.zebrajaeger.phserver.stomp;


import de.zebrajaeger.phserver.StepsToDeg;
import de.zebrajaeger.phserver.data.PanoHeadData;
import de.zebrajaeger.phserver.event.PanoHeadDataEvent;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PanoHeadState {
    private final double x;
    private final double y;

    public static PanoHeadState of(PanoHeadData data){
        return new PanoHeadState(data);
    }
    public static PanoHeadState of(PanoHeadDataEvent event){
        return new PanoHeadState(event.getData());
    }

    public PanoHeadState(PanoHeadData data) {
        this.x = StepsToDeg.INSTANCE.translate(data.getActor().getX().getPos());
        this.y = StepsToDeg.INSTANCE.translate(data.getActor().getY().getPos());
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

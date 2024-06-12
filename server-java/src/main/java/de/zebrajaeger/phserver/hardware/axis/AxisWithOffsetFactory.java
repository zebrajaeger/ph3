package de.zebrajaeger.phserver.hardware.axis;

import de.zebrajaeger.phserver.data.AxisIndex;
import de.zebrajaeger.phserver.hardware.Actor;
import de.zebrajaeger.phserver.translation.AxisParameters;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile({"locali2c","remotei2c", "develop"})
@Service
public class AxisWithOffsetFactory implements  AxisFactory{
    private final Actor actor;

    public AxisWithOffsetFactory(Actor actor) {
        this.actor = actor;
    }
    public Axis create(AxisIndex axisIndex, AxisParameters axisParameters){
        return new AxisWithOffset(actor, axisIndex, axisParameters);
    }
}

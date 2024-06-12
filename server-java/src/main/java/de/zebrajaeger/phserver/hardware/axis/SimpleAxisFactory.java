package de.zebrajaeger.phserver.hardware.axis;

import de.zebrajaeger.phserver.data.AxisIndex;
import de.zebrajaeger.phserver.hardware.Actor;
import de.zebrajaeger.phserver.translation.AxisParameters;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("mqtt")
@Service
public class SimpleAxisFactory implements AxisFactory {
    private final Actor actor;

    public SimpleAxisFactory(Actor actor) {
        this.actor = actor;
    }

    public Axis create(AxisIndex axisIndex, AxisParameters axisParameters) {
        return new SimpleAxis(actor, axisIndex, axisParameters);
    }
}

package de.zebrajaeger.phserver.hardware.axis;


import de.zebrajaeger.phserver.data.AxisIndex;
import de.zebrajaeger.phserver.hardware.actor.Actor;
import de.zebrajaeger.phserver.translation.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("v5")
@Component
public class PhV5 {
    private final Actor actor;

    public PhV5(Actor actor) {
        this.actor = actor;
    }

    @Bean
    public Axis x() {
        final AxisParameters axisParameters = new AxisParameters(
                new DefaultStepperParameters(),
                MotorDriverParameters.MDP_16,
                new BeltGearParameters(15,60), true);
        return new AxisWithOffset(actor, AxisIndex.X, axisParameters);
    }

    @Bean
    public Axis y() {
        final AxisParameters axisParameters = new AxisParameters(
                new DefaultStepperParameters(350),
                MotorDriverParameters.MDP_16,
                new BeltGearParameters(15,60), false);
        return new AxisWithOffset(actor, AxisIndex.X, axisParameters);
    }
}

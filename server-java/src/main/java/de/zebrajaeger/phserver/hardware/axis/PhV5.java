package de.zebrajaeger.phserver.hardware.axis;


import de.zebrajaeger.phserver.data.AxisIndex;
import de.zebrajaeger.phserver.hardware.actor.Actor;
import de.zebrajaeger.phserver.translation.AxisParameters;
import de.zebrajaeger.phserver.translation.BeltGearParameters;
import de.zebrajaeger.phserver.translation.DefaultStepperParameters;
import de.zebrajaeger.phserver.translation.MotorDriverParameters;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("v5")
@Component
public class PhV5 {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final Actor actor;

    public PhV5(ApplicationEventPublisher applicationEventPublisher, Actor actor) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.actor = actor;
    }

    @Bean
    public Axis x() {
        final AxisParameters axisParameters = new AxisParameters(
                new DefaultStepperParameters(60),
                MotorDriverParameters.MDP_16,
                new BeltGearParameters(15, 80), true);
        return new AxisWithOffset(applicationEventPublisher, actor, AxisIndex.X, axisParameters);
    }

    @Bean
    public Axis y() {
        final AxisParameters axisParameters = new AxisParameters(
                new DefaultStepperParameters(60),
                MotorDriverParameters.MDP_16,
                new BeltGearParameters(15, 80), false);
        return new AxisWithOffset(applicationEventPublisher, actor, AxisIndex.Y, axisParameters);
    }
}

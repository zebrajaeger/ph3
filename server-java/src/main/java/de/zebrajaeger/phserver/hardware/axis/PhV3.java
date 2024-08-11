package de.zebrajaeger.phserver.hardware.axis;


import de.zebrajaeger.phserver.data.AxisIndex;
import de.zebrajaeger.phserver.hardware.actor.Actor;
import de.zebrajaeger.phserver.translation.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

//@Profile({"!v5"})

@Component
@ConditionalOnProperty(name="enable.ph.version", havingValue = "v3")
public class PhV3 {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final Actor actor;

    public PhV3(ApplicationEventPublisher applicationEventPublisher, Actor actor) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.actor = actor;
    }

    @Bean
    public Axis x() {
        final AxisParameters axisParameters = new AxisParameters(
                new DefaultStepperParameters(),
                MotorDriverParameters.MDP_16,
                new SpurGearParameters(), true);
        return new AxisWithOffset(applicationEventPublisher, actor, AxisIndex.X, axisParameters);
    }

    @Bean
    public Axis y() {
        final AxisParameters axisParameters = new AxisParameters(
                new DefaultStepperParameters(350),
                MotorDriverParameters.MDP_16,
                new WormGearParameters(), false);
        return new AxisWithOffset(applicationEventPublisher, actor, AxisIndex.X, axisParameters);
    }
}

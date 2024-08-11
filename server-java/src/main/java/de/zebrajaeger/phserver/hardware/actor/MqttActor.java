package de.zebrajaeger.phserver.hardware.actor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.zebrajaeger.phserver.data.*;
import de.zebrajaeger.phserver.event.MqttEvent;
import de.zebrajaeger.phserver.event.ActorStatusEvent;
import de.zebrajaeger.phserver.event.PowerMeasureEvent;
import de.zebrajaeger.phserver.hardware.mqtt.MqttConnectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

//@Profile({"mqtt-actor"})
@Service
@ConditionalOnProperty("enable.actor.mqtt")
@Slf4j
public class MqttActor implements Actor {
    private final MqttConnectionService mqttConnectionService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MqttActor(MqttConnectionService mqttConnectionService, ApplicationEventPublisher applicationEventPublisher) {
        this.mqttConnectionService = mqttConnectionService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @EventListener
    public void mqttEvent(MqttEvent event) throws JsonProcessingException {
        switch (event.topic()) {
            case ACTOR -> {
                MqttActorStatus mqttActorStatus = objectMapper.readValue(event.message(), MqttActorStatus.class);
                ActorStatus data = new ActorStatus();
//                data.getCamera().setFocus(mqttPhStatus.c().focus());
//                data.getCamera().setTrigger(mqttPhStatus.c().trigger());

                data.getX().setMoving(mqttActorStatus.x().running());
                // fix for FastAccelStepper::getCurrentSpeedInMilliHz never reaches zero
                data.getX().setSpeed(mqttActorStatus.x().running() ? mqttActorStatus.x().speed() / 1000 : 0);
                data.getX().setPos(mqttActorStatus.x().pos());

                // fix for FastAccelStepper::getCurrentSpeedInMilliHz never reaches zero
                data.getY().setMoving(mqttActorStatus.y().running());
                data.getY().setSpeed(mqttActorStatus.y().running() ? mqttActorStatus.y().speed() / 1000 : 0);
                data.getY().setPos(mqttActorStatus.y().pos());

//                log.info("PHData {}", data);
                applicationEventPublisher.publishEvent(new ActorStatusEvent(data));
            }
            case POWER -> {
                Power power = objectMapper.readValue(event.message(), Power.class);
                applicationEventPublisher.publishEvent(new PowerMeasureEvent(power));
            }
            case UNKNOWN -> {
                log.warn("Unknown topic receive. Should it be processed?: {}/{}", event.topic(), event.rawTopic());
            }
        }
    }

    @Override
    public void setLimit(AxisIndex axisIndex, int velocityMinHz, int velocityMaxHz, int accelerationMaxHzPerSecond) throws IOException {
        // TODO
    }

    private int currentXVelocity = 0;
    private int currentYVelocity = 0;

    @Override
    public void setTargetVelocity(AxisIndex axisIndex, int velocity) throws Exception {
        if (axisIndex == AxisIndex.X) {
//            if (velocity == 0) {
//                mqttConnectionService.send(MqttCommand.stop());
//            } else {
            if (currentXVelocity != velocity) {
                mqttConnectionService.send(MqttCommand.speedX(velocity));
                currentXVelocity = velocity;
            }
//            }
        } else if (axisIndex == AxisIndex.Y) {
            if (currentYVelocity != velocity) {
                mqttConnectionService.send(MqttCommand.speedY(velocity));
                currentYVelocity = velocity;
            }

//            if (velocity == 0) {
//                mqttConnectionService.send(MqttCommand.stop());
//            } else {
//                mqttConnectionService.send(MqttCommand.speedY(velocity));
//            }
        }
    }

    @Override
    public void setTargetPos(AxisIndex axisIndex, int pos) throws Exception {
        if (axisIndex == AxisIndex.X) {
            mqttConnectionService.send(MqttCommand.moveToX(pos));
        } else if (axisIndex == AxisIndex.Y) {
            mqttConnectionService.send(MqttCommand.moveToY(pos));
        }
    }

    @Override
    public void stopAll() throws Exception {
        mqttConnectionService.send(MqttCommand.stop());
    }

//    @Override
//    public void setActualPos(AxisIndex axisIndex, int pos) throws IOException {
//
//    }

    @Override
    public void setActualAndTargetPos(AxisIndex axisIndex, int pos) throws Exception {
        if (axisIndex == AxisIndex.X) {
            mqttConnectionService.send(MqttCommand.setPosX(pos));
        } else if (axisIndex == AxisIndex.Y) {
            mqttConnectionService.send(MqttCommand.setPosY(pos));
        }
    }

    @Override
    public void resetPos() throws Exception {
        mqttConnectionService.send(MqttCommand.setPos(0, 0));
    }
}

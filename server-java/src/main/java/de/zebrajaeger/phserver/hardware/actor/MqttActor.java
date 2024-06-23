package de.zebrajaeger.phserver.hardware.actor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.zebrajaeger.phserver.data.*;
import de.zebrajaeger.phserver.event.MqttEvent;
import de.zebrajaeger.phserver.event.PanoHeadDataEvent;
import de.zebrajaeger.phserver.event.PowerMeasureEvent;
import de.zebrajaeger.phserver.hardware.mqtt.MqttConnectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Profile("mqtt")
@Service
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
            case STATUS -> {
                MqttPhStatus mqttPhStatus = objectMapper.readValue(event.message(), MqttPhStatus.class);
                PanoHeadData data = new PanoHeadData();
                data.getCamera().setFocus(mqttPhStatus.c().focus());
                data.getCamera().setTrigger(mqttPhStatus.c().trigger());

                data.getActorStatus().getX().setMoving(mqttPhStatus.x().running());
                // fix for FastAccelStepper::getCurrentSpeedInMilliHz never reaches zero
                data.getActorStatus().getX().setSpeed(mqttPhStatus.x().running() ? mqttPhStatus.x().speed() : 0);
                data.getActorStatus().getX().setPos(mqttPhStatus.x().pos());

                // fix for FastAccelStepper::getCurrentSpeedInMilliHz never reaches zero
                data.getActorStatus().getY().setMoving(mqttPhStatus.y().running());
                data.getActorStatus().getX().setSpeed(mqttPhStatus.y().running() ? mqttPhStatus.y().speed() : 0);
                data.getActorStatus().getY().setPos(mqttPhStatus.y().pos());

                log.info("PHData {}", data);
                applicationEventPublisher.publishEvent(new PanoHeadDataEvent(data));
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
    public void startFocus(int focusTimeMs) throws Exception {
        mqttConnectionService.send(MqttCommand.focus(focusTimeMs));
    }

    @Override
    public void startTrigger(int triggerTimeMs) throws Exception {
        mqttConnectionService.send(MqttCommand.trigger(triggerTimeMs));
    }

    @Override
    public void startShot(int focusTimeMs, int triggerTimeMs) throws Exception {
        mqttConnectionService.send(MqttCommand.shot(focusTimeMs, triggerTimeMs));
    }

    @Override
    public void setLimit(AxisIndex axisIndex, int velocityMinHz, int velocityMaxHz, int accelerationMaxHzPerSecond) throws IOException {
        // TODO
    }

    @Override
    public void setTargetVelocity(AxisIndex axisIndex, int velocity) throws Exception {
        if (axisIndex == AxisIndex.X) {
            mqttConnectionService.send(MqttCommand.speedX(velocity));
        } else if (axisIndex == AxisIndex.Y) {
            mqttConnectionService.send(MqttCommand.speedY(velocity));
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
    public void setActualAndTargetPos(AxisIndex axisIndex, int pos) throws IOException {
        throw new UnsupportedOperationException();
    }

//    @Override
//    public void resetPos() throws Exception {
//
//    }
}

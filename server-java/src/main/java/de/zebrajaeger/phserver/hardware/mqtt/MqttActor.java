package de.zebrajaeger.phserver.hardware.mqtt;

import de.zebrajaeger.phserver.data.AxisIndex;
import de.zebrajaeger.phserver.data.MqttCommand;
import de.zebrajaeger.phserver.event.MqttEvent;
import de.zebrajaeger.phserver.hardware.Actor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Profile("mqtt")
@Service
@Slf4j
public class MqttActor implements Actor {
    private final MqttConnectionService mqttConnectionService;

    public MqttActor(MqttConnectionService mqttConnectionService) {
        this.mqttConnectionService = mqttConnectionService;
    }

    @EventListener
    public void mqttEvent(MqttEvent event) {
        // STATUS PanoHeadDataEvent
        // Power PowerMeasureEvent
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
            mqttConnectionService.send(MqttCommand.moveY(pos));
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

    }

//    @Override
//    public void resetPos() throws Exception {
//
//    }
}

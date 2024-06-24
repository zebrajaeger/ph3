package de.zebrajaeger.phserver.hardware.actor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.zebrajaeger.phserver.data.CameraStatus;
import de.zebrajaeger.phserver.data.MqttCameraStatus;
import de.zebrajaeger.phserver.data.MqttCommand;
import de.zebrajaeger.phserver.event.CameraChangedEvent;
import de.zebrajaeger.phserver.event.MqttEvent;
import de.zebrajaeger.phserver.hardware.mqtt.MqttConnectionService;
import de.zebrajaeger.phserver.hardware.mqtt.MqttTopic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Profile({"mqtt & !ccapi"})
@Service
@Slf4j
public class MqttCamera implements Camera {
    private final MqttConnectionService mqttConnectionService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public MqttCamera(MqttConnectionService mqttConnectionService, ApplicationEventPublisher applicationEventPublisher) {
        this.mqttConnectionService = mqttConnectionService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @EventListener
    public void mqttEvent(MqttEvent event) throws JsonProcessingException {
        if (MqttTopic.CAMERA == event.topic()) {
            MqttCameraStatus mqttActorStatus = objectMapper.readValue(event.message(), MqttCameraStatus.class);
            CameraStatus cameraStatus = new CameraStatus();
            cameraStatus.setFocus(mqttActorStatus.focus());
            cameraStatus.setTrigger(mqttActorStatus.trigger());

            log.info("CameraStatus {}", cameraStatus);
            applicationEventPublisher.publishEvent(new CameraChangedEvent(cameraStatus));
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
}

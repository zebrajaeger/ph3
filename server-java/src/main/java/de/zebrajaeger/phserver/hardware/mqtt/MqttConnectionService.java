package de.zebrajaeger.phserver.hardware.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.zebrajaeger.phserver.data.MqttCommand;
import de.zebrajaeger.phserver.event.MqttEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Profile("mqtt")
@Service
@Slf4j
public class MqttConnectionService implements MqttCallback{

    private IMqttClient mqttClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${mqtt.server.url:tcp://192.168.8.142:1883}")
    private String mqttServerUrl;
    @Value("${mqtt.topic.status:ph5/cmd}")
    private String commandTopic;
    @Value("${mqtt.topic.status:ph5/status}")
    private String statusTopic;
    @Value("${mqtt.topic.status:ph5/power}")
    private String powerTopic;

    private final ApplicationEventPublisher applicationEventPublisher;

    public MqttConnectionService(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void send(MqttCommand cmd) throws MqttException, JsonProcessingException {
        MqttMessage msg = new MqttMessage();
        msg.setQos(1);
        msg.setRetained(false);
        msg.setPayload(objectMapper.writeValueAsBytes(cmd));
        log.info("Send MQTT Command {}", msg);
        mqttClient.publish(commandTopic, msg);
    }

    private void sendString(String topic, String message) throws MqttException {
        MqttMessage msg = new MqttMessage();
        msg.setQos(1);
        msg.setRetained(false);
        msg.setPayload(message.getBytes(StandardCharsets.UTF_8));
        mqttClient.publish(topic, msg);
    }

    @PostConstruct
    private void init() throws MqttException {
        log.info("Connect to MQTT");
        mqttClient = new MqttClient(mqttServerUrl, "ph5-" + System.currentTimeMillis());

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        mqttClient.connect(options);

        mqttClient.subscribe(statusTopic);
        mqttClient.setCallback(this);
    }

    @PreDestroy
    private void destroy() throws MqttException {
        mqttClient.disconnect();
        mqttClient.close();
    }

    @Override
    public void connectionLost(Throwable cause) {
        log.warn("MQTT connection lost", cause);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = message.toString();
        if(statusTopic.equals(topic)){
            applicationEventPublisher.publishEvent(new MqttEvent(topic, MqttTopic.STATUS, payload));
        }else if(powerTopic.equals(topic)){
            applicationEventPublisher.publishEvent(new MqttEvent(topic, MqttTopic.POWER, payload));
        }
//                applicationEventPublisher.publishEvent(objectMapper.readValue(payload, MqttPhStatus.class));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    // ignore
    }
}

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
public class MqttConnectionService implements MqttCallbackExtended {

    private IMqttClient mqttClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
//    @Value("${mqtt.server.url:ws://192.168.8.144:1883,ws://192.168.8.142:1883}")
    @Value("${mqtt.server.url:tcp://192.168.8.144:1883,tcp://192.168.8.142:1883}")
    private String mqttServerUrl;
    @Value("${mqtt.topic.status:ph5/cmd}")
    private String commandTopic;
    @Value("${mqtt.topic.status:ph5/actor}")
    private String actorTopic;
    @Value("${mqtt.topic.status:ph5/power}")
    private String powerTopic;
    @Value("${mqtt.topic.status:ph5/camera}")
    private String cameraTopic;

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
        log.info("Connect to first available host of '{}'", mqttServerUrl);
        String[] hosts = mqttServerUrl.split(",");
        if (hosts.length == 0) {
            throw new IllegalArgumentException("No mqtt url in mqtt.server.url:" + mqttServerUrl);
        }

        mqttClient = new MqttClient(hosts[0], "ph5-" + System.currentTimeMillis());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(5);
        options.setServerURIs(hosts);
        mqttClient.setCallback(this);

        log.info("Connect to MQTT");
        mqttClient.connect(options);
        log.info("Connect to MQTT - Done");

        mqttClient.subscribe(actorTopic);
        mqttClient.subscribe(cameraTopic);
        mqttClient.subscribe(powerTopic);
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
        if (actorTopic.equals(topic)) {
            applicationEventPublisher.publishEvent(new MqttEvent(topic, MqttTopic.ACTOR, payload));
        } else if (cameraTopic.equals(topic)) {
            applicationEventPublisher.publishEvent(new MqttEvent(topic, MqttTopic.CAMERA, payload));
        } else if (powerTopic.equals(topic)) {
            applicationEventPublisher.publishEvent(new MqttEvent(topic, MqttTopic.POWER, payload));
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // ignore
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        log.info("Connected to MQTT host: '{}', reconnect: {}", serverURI, reconnect);
    }
}

package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.hardware.mqtt.MqttTopic;

public record MqttEvent(String rawTopic, MqttTopic topic,  String message) {

}

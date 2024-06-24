package de.zebrajaeger.phserver.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MqttActorStatus(
        long t,
        MqttPhStatusAxis x,
        MqttPhStatusAxis y) {
}

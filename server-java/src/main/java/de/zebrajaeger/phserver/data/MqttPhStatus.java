package de.zebrajaeger.phserver.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MqttPhStatus(
        long t,
        MqttPhStatusAxis x,
        MqttPhStatusAxis y,
        MqttPhStatusCamera c) {
}

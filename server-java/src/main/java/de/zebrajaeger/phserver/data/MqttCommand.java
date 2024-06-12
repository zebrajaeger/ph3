package de.zebrajaeger.phserver.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public record MqttCommand(
        Integer nr,
        String cmd,
        Integer x,
        Integer y,
        Integer speedX,
        Integer speedY,
        Integer focus,
        Integer trigger) {
    public static MqttCommand speed(int x, int y) {
        return new MqttCommand(null, "speed", x, y, null, null, null, null);
    }

    public static MqttCommand speedX(int x) {
        return new MqttCommand(null, "speed", x, null, null, null, null, null);
    }

    public static MqttCommand speedY(int y) {
        return new MqttCommand(null, "speed", null, y, null, null, null, null);
    }

    public static MqttCommand move(int x, int y) {
        return new MqttCommand(null, "move", x, y, null, null, null, null);
    }

    public static MqttCommand moveX(int x) {
        return new MqttCommand(null, "move", x, null, null, null, null, null);
    }

    public static MqttCommand moveY(int y) {
        return new MqttCommand(null, "move", null, y, null, null, null, null);
    }

    public static MqttCommand moveTo(int x, int y) {
        return new MqttCommand(null, "moveTo", x, y, null, null, null, null);
    }

    public static MqttCommand moveToX(int x) {
        return new MqttCommand(null, "moveTo", x, null, null, null, null, null);
    }

    public static MqttCommand moveToY(int y) {
        return new MqttCommand(null, "moveTo", null, y, null, null, null, null);
    }

    public static MqttCommand shot(int focus, int trigger) {
        return new MqttCommand(null, "shot", null, null, null, null, focus, trigger);
    }

    public static MqttCommand focus(int focus) {
        return new MqttCommand(null, "shot", null, null, null, null, focus, null);
    }

    public static MqttCommand trigger(int trigger) {
        return new MqttCommand(null, "shot", null, null, null, null, null, trigger);
    }

    public static MqttCommand stop() {
        return new MqttCommand(null, "stop", null, null, null, null, null, null);
    }

    public static MqttCommand forceStop() {
        return new MqttCommand(null, "forceStop", null, null, null, null, null, null);
    }
}

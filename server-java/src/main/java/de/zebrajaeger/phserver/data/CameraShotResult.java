package de.zebrajaeger.phserver.data;

public record CameraShotResult(boolean successfully, String message, Exception exception) {
}

package de.zebrajaeger.phserver.record;

public record RecordStateEvent(RecordState state, Exception error) {
}

package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.hardware.axis.Axis;

public record AxisChangedEvent(Axis axis) {
}

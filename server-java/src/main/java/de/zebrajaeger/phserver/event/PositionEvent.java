package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.data.Position;
import de.zebrajaeger.phserver.data.RawPosition;

public record PositionEvent(RawPosition targetRawPosition, Position targetDegPosition,
                            RawPosition measuredRawPosition, Position measuredDegPosition) {
}

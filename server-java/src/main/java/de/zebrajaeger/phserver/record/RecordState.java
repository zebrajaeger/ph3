package de.zebrajaeger.phserver.record;

import de.zebrajaeger.phserver.pano.Command;

import java.util.Collection;

public record RecordState(Collection<States> states, Command command, int commandIndex, int commandCount) {
}

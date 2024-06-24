package de.zebrajaeger.phserver.hardware.actor;

import de.zebrajaeger.phserver.data.ActorData;
import de.zebrajaeger.phserver.data.ActorStatus;

import java.io.IOException;

public interface ReadableActor{
    void update() throws IOException;
    ActorStatus readActorStatus();
}

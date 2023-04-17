package de.zebrajaeger.phserver.event;

import de.zebrajaeger.phserver.data.PanoHeadData;
import lombok.Data;

@Data
public class PanoHeadDataEvent {

  private final PanoHeadData data;
}

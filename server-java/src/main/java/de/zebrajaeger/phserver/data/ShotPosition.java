package de.zebrajaeger.phserver.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class ShotPosition {

  private double x;
  private double y;
  private final int index;
  @JsonProperty("xIndex")
  private final int xIndex;
  @JsonProperty("xLength")
  private final int xLength;
  @JsonProperty("yIndex")
  private final int yIndex;
  @JsonProperty("yLength")
  private final int yLength;
}

package de.zebrajaeger.phserver.data;

import java.util.LinkedList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CalculatedPano extends Pano {

  private Position startPosition;

  private List<Double> horizontalPositions = new LinkedList<>();
  private List<Double> verticalPositions = new LinkedList<>();

  private double horizontalOverlap;
  private double verticalOverlap;

  public CalculatedPano(Pano pano) {
    super(pano);
  }

  public int hSize(){
    return horizontalPositions.size();
  }
  public int vSize(){
    return verticalPositions.size();
  }
}

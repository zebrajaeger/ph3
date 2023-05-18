package de.zebrajaeger.phserver.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PanoMatrix {

  private final List<Double> yPositions = new ArrayList<>();
  private final List<List<Double>> xPositions = new ArrayList<>();

  @JsonProperty("ySize")
  public int getYSize() {
    return yPositions.size();
  }

  @JsonProperty("minY")
  public Double getMinY() {
    return yPositions.stream().min(Double::compareTo).orElse(null);
  }

  @JsonProperty("maxY")
  public Double getMaxY() {
    return yPositions.stream().max(Double::compareTo).orElse(null);
  }

  public void setYPositions(List<Double> value) {
    xPositions.clear();
    value.forEach(this::addY);
  }

  public void setAllXPositions(List<Double> value) {
    xPositions.clear();
    for (int i = 0; i < yPositions.size(); ++i) {
      xPositions.add(value);
    }
  }

  @JsonProperty("minX")
  public Double getMinX() {
    return xPositions.stream()
        .flatMap(Collection::stream)
        .min(Double::compareTo)
        .orElse(0d);
  }

  @JsonProperty("maxX")
  public Double getMaxX() {

    return xPositions.stream()
        .flatMap(Collection::stream)
        .max(Double::compareTo)
        .orElse(0d);
  }

  public int getXSize(int yIndex) {
    return xPositions.get(yIndex).size();
  }

  @JsonProperty("maxXSize")
  public int getMaxXSize() {
    return xPositions.stream()
        .map(List::size)
        .max(Integer::compareTo)
        .orElse(0);
  }

  @JsonProperty("positionCount")
  public int getPositionCount() {
    return xPositions.stream()
        .map(List::size)
        .reduce(0, Integer::sum);
  }

  public void addY(Double value) {
    yPositions.add(value);
    xPositions.add(new ArrayList<>());
  }

  @JsonProperty("xPositions")
  public List<List<Double>> getXPositions() {
    return Collections.unmodifiableList(xPositions);
  }

  public List<Double> getXPositions(int index) {
    return Collections.unmodifiableList(xPositions.get(index));
  }

  @JsonProperty("yPositions")
  public List<Double> getYPositions() {
    return Collections.unmodifiableList(yPositions);
  }

  public void addToRow(int yIndex, Double value) {
    getXPositions(yIndex).add(value);
  }

  public List<Position> asPositionList(boolean xInverted, boolean yInverted) {
    List<Position> result = new ArrayList<>();
    if (yInverted) {
      for (int iy = yPositions.size() - 1; iy >= 0; --iy) {
        Double y = yPositions.get(iy);
        List<Double> xs = getXPositions(iy);
        result.addAll(addXPositions(y, xs, xInverted));
      }
    } else {
      for (int iy = 0; iy < yPositions.size(); ++iy) {
        Double y = yPositions.get(iy);
        List<Double> xs = getXPositions(iy);
        result.addAll(addXPositions(y, xs, xInverted));
      }
    }
    return result;
  }

  private List<Position> addXPositions(Double y, List<Double> xs, boolean xInverted) {
    List<Position> result = new ArrayList<>();

    if (xInverted) {
      for (int ix = xs.size() - 1; ix >= 0; --ix) {
        Double x = xs.get(ix);
        result.add(new Position(x, y));
      }
    } else {
      for (Double x : xs) {
        result.add(new Position(x, y));
      }
    }

    return result;
  }
}

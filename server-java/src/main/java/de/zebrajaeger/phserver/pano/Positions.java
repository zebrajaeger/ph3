package de.zebrajaeger.phserver.pano;

import de.zebrajaeger.phserver.data.ShotPosition;
import java.util.ArrayList;
import java.util.List;

public class Positions {

  private final ShotPosition[] shotPositions;
  private final int xSize;
  private final int ySize;

  public Positions(int xSize, int ySize) {
    this.xSize = xSize;
    this.ySize = ySize;
    shotPositions = new ShotPosition[xSize * ySize];
  }

  public void add(ShotPosition shotPosition) {
    final int x = shotPosition.getXIndex();
    final int y = shotPosition.getYIndex();
    shotPositions[y * xSize + x] = shotPosition;
  }

  public ShotPosition get(int xIndex, int yIndex) {
    return shotPositions[xSize * yIndex + xIndex];
  }

  public List<ShotPosition> getRow(int yIndex) {
    List<ShotPosition> result = new ArrayList<>(xSize);
    for (int i = 0; i < xSize; ++i) {
      result.add(get(i, yIndex));
    }
    return result;
  }

  public List<ShotPosition> getColumn(int xIndex) {
    List<ShotPosition> result = new ArrayList<>(xSize);
    for (int i = 0; i < ySize; ++i) {
      result.add(get(xIndex, i));
    }
    return result;
  }

  public List<ShotPosition> getAll() {
    return getAll(false, true);
  }

  public List<ShotPosition> getAll(boolean leftToRightInverse, boolean topToBottonInverse) {
    int[] xIs = createIndexArray(leftToRightInverse, xSize);
    int[] yIs = createIndexArray(topToBottonInverse, ySize);

    List<ShotPosition> result = new ArrayList<>(xSize * ySize);
    for (int y : yIs) {
      for (int x : xIs) {
        result.add(get(x, y));
      }
    }

    return result;
  }

  private int[] createIndexArray(boolean inverse, int length) {
    int[] indexes = new int[length];
    if (!inverse) {
      for (int i = 0; i < length; ++i) {
        indexes[i] = i;
      }
    } else {
      for (int i = 0; i < length; ++i) {
        indexes[i] = length - i - 1;
      }
    }
    return indexes;
  }
}

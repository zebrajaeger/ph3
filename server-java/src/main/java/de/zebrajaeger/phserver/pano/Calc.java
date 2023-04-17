package de.zebrajaeger.phserver.pano;

import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.Assert;

@Getter
@Setter
public class Calc {

  private boolean partial = false;
  private Double targetStartPoint;
  private Double targetSize;
  private Double sourceSize;
  private Double overlap;

  @Getter
  public static class Result {

    private int n;
    private double overlap;
    private List<Double> startPositions;

    @Override
    public String toString() {
      return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
  }

  public void reset() {
    partial = false;
    targetStartPoint = null;
    targetSize = null;

    sourceSize = null;
    overlap = null;
  }

  public Result calc() {
    Result r = new Result();
    r.n = this.calcN();
    r.overlap = this.calcOverlap(r.n);
    r.startPositions = this.calcStartPositions(r.n, r.overlap);
    return r;
  }

  private int calcN() {
    double b = sourceSize;
    double d = targetSize;
    double o = overlap;

    double n;
    if (partial) {
      n = (targetSize < sourceSize)
          ? 1
          : (-b * o + d) / (-b * o + b);
    } else {
      n = d / (-b * o + b);
    }
    return (int) Math.ceil(n);
  }

  private double calcOverlap(int n) {
    double b = this.sourceSize;
    double d = this.targetSize;

    double overlap;
    if (partial) {
//            overlap = (-b * n + d) / (-b * n + b);
      overlap = (-b * n + d) / n;
    } else {
//            double allSourceImages = this.sourceSize * n;
//            double overlapAll = allSourceImages - this.targetSize;
//            double overlapPerSource = overlapAll / n;
//            double result = overlapPerSource / this.sourceSize;
//            return result;
      overlap = (b * n - d) / (b * n);
    }
    return Math.max(0, overlap);
  }

  public List<Double> calcStartPositions(int n, double overlap) {
    List<Double> result = new LinkedList<>();
    if (n == 1) {
      double v1 = targetStartPoint;
      double v2 = targetStartPoint + targetSize;
      result.add((v1 + v2) / 2);

    } else if (partial) {
      double v1 = targetStartPoint + sourceSize / 2;
      double v2 = targetStartPoint + targetSize - sourceSize / 2;
      double d = (v2 - v1) / (n - 1);

      result.add(v1);
      for (int i = 1; i < n - 1; ++i) {
        result.add(v1 + (d * i));
      }
      result.add(v2);

    } else {
      double v1 = targetStartPoint + sourceSize / 2;
      double d = 360d / n;
      for (int i = 0; i < n; ++i) {
        result.add(v1 + (d * i));
      }
    }

    return result;
  }

  public void setOverlap(Double overlap) {
    Assert.state(overlap >= 0d, "Overlap must be greater or equal zero ");
    Assert.state(overlap < 1d, "Overlap must be less than zero ");
    this.overlap = overlap;
  }
}

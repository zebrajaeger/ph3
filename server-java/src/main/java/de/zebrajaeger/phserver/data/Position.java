package de.zebrajaeger.phserver.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Position {
  private double x;
  private double y;

  public Position add(Position other) {
    return new Position(x + other.x, y + other.y);
  }

  public Position add(Angle other) {
    return new Position(x + other.getX(), y + other.getY());
  }

  public Position withBorderOfOne() {
    return new Position(withBorder(x, -1, 1), withBorder(y, -1, 1));
  }

  private double withBorder(double value,
      @SuppressWarnings("SameParameterValue") double min,
      @SuppressWarnings("SameParameterValue") double max) {
    if (value < min) {
      return min;
    }
    return Math.min(value, max);
  }
}

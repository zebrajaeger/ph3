package de.zebrajaeger.phserver.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Range {

  private Double from;
  private Double to;

  public Range(Range other) {
    this.from = other.from;
    this.to = other.to;
  }

  public boolean isComplete() {
    return from != null && to != null;
  }

  public Double getSize() {
    return isComplete() ? to - from : null;
  }

  public Range normalize() {
    return (isComplete() && from > to) ? new Range(to, from) : new Range(from, to);
  }
}

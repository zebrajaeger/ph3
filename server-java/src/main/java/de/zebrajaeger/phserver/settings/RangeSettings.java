package de.zebrajaeger.phserver.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.zebrajaeger.phserver.data.Range;
import lombok.Data;

@Data
public class RangeSettings {

  private Double from;
  private Double to;

  @JsonIgnore
  public void getAll(Range range) {
    range.setFrom(from);
    range.setTo(to);
  }

  @JsonIgnore
  public void setAll(Range range) {
    this.from = range.getFrom();
    this.to = range.getTo();
  }
}

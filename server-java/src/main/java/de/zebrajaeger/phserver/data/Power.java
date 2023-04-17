package de.zebrajaeger.phserver.data;

import lombok.Data;

@Data
public class Power {

  private final Double voltage;
  private final Double current;
  private final Double power;

  public Power(Double voltage, Double current) {
    this.voltage = voltage;
    this.current = current;
    this.power = voltage * current;
  }

  public Power(Double power) {
    this.voltage = null;
    this.current = null;
    this.power = power;
  }
}

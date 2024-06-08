package de.zebrajaeger.phserver.data;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Power {

    private final Double voltage;
    private final Double current;
    private final Double power;

    public Power(Double voltage, Double current) {
        this.voltage = voltage;
        this.current = current;
        this.power = voltage * current;
    }
}

package de.zebrajaeger.phserver.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DoubleBorder {
    @JsonProperty("from")
    private double bFrom;
    @JsonProperty("to")
    private double bTo;

    public double getFrom() {
        return bFrom;
    }

    public void setFrom(double bFrom) {
        this.bFrom = bFrom;
    }

    public double getTo() {
        return bTo;
    }

    public void setTo(double bto) {
        this.bTo = bto;
    }
}

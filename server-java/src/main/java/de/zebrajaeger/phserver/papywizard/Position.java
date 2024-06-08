package de.zebrajaeger.phserver.papywizard;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Position {

    public static Position of(double x, double y) {
        Position p = new Position();
        p.setX(x);
        p.setY(y);
        return p;
    }

    // x
    @JacksonXmlProperty(isAttribute = true)
    private double yaw;

    // y
    @JacksonXmlProperty(isAttribute = true)
    private double pitch;

    @JacksonXmlProperty(isAttribute = true)
    private double roll = 0d;

    public void setX(double x) {
        yaw = x;
    }

    public void setY(double y) {
        pitch = y;
    }
}

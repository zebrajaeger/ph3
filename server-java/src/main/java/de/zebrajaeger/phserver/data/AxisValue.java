package de.zebrajaeger.phserver.data;

import lombok.Data;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@Data
public class AxisValue {
    private int axisIndex;
    private int value;
}

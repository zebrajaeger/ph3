package de.zebrajaeger.phserver.hardware.axis;

import de.zebrajaeger.phserver.data.AxisIndex;
import de.zebrajaeger.phserver.translation.AxisParameters;

public interface AxisFactory {
    Axis create(AxisIndex axisIndex, AxisParameters axisParameters);
}

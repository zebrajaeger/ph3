package de.zebrajaeger.phserver.data;

import de.zebrajaeger.phserver.util.MathUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class JoystickAxis {
    private float rawValue = 0;
    private float mappedValue = 0;
    private float cutValue = 0;
    private float cutBorder = 0.05f;

    private final AutoRange autoRange = new AutoRange();

    public void reset() {
        rawValue = 0;
        mappedValue = 0;
        cutValue = 0;
        autoRange.reset();
    }

    public void setRawValue(float rawValue) {
        this.rawValue = rawValue;
        autoRange.updateAutoRangeWithRawValue(rawValue);
        mappedValue = autoRange.rawValueToRange(rawValue);
        if (mappedValue > cutBorder) {
            cutValue = MathUtils.map(mappedValue, cutBorder, 1f, 0f, 1f);
        } else if (mappedValue < -cutBorder) {
            cutValue = MathUtils.map(mappedValue, -1f, -cutBorder, -1f, 0f);
        } else {
            cutValue = 0;
        }
    }

    public void setRawValueAsCenter(float rawValue) {
        autoRange.setRawValueAsCenter(rawValue);
    }

    public void setCutBorder(float cutBorder) {
        this.cutBorder = cutBorder;
    }

    public float getRawValue() {
        return rawValue;
    }

    public float getMappedValue() {
        return mappedValue;
    }

    public float getCutValue() {
        return cutValue;
    }

    public float getCutBorder() {
        return cutBorder;
    }

    public AutoRange getAutoRange() {
        return autoRange;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}

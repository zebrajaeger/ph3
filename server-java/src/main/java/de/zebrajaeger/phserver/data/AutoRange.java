package de.zebrajaeger.phserver.data;

import de.zebrajaeger.phserver.util.MathUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class AutoRange {
    private float rawMin = Float.MAX_VALUE;
    private float rawCenter = 0f;
    private float rawMax = -Float.MAX_VALUE;

    public void reset() {
        rawMin = Float.MAX_VALUE;
        rawCenter = 0f;
        rawMax = Float.MIN_VALUE;
    }

    public void setRawValueAsCenter(float rawValue) {
        this.rawCenter = rawValue;
    }

    public void updateAutoRangeWithRawValue(float rawValue) {
        if (rawMin > rawValue) {
            rawMin = rawValue;
        }
        if (rawMax < rawValue) {
            rawMax = rawValue;
        }
    }

    public float rawValueToRange(float rawValue) {
        if (rawValue > rawCenter) {
            return MathUtils.map(rawValue, rawCenter, rawMax, 0f, 1f);
        } else {
            return MathUtils.map(rawValue, rawMin, rawCenter, -1f, 0f);
        }
    }

    public float getRawMin() {
        return rawMin;
    }

    public float getRawCenter() {
        return rawCenter;
    }

    public float getRawMax() {
        return rawMax;
    }

    public void setAll(float rawMin, float rawCenter, float rawMax){
        this.rawMin = rawMin;
        this.rawCenter = rawCenter;
        this.rawMax = rawMax;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}

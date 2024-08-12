package de.zebrajaeger.phserver.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CameraStatus {
    protected boolean focus;
    protected boolean trigger;

    public CameraStatus(CameraStatus other) {
        this.focus = other.focus;
        this.trigger = other.trigger;
    }
}

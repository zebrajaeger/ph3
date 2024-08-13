package de.zebrajaeger.phserver.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CameraStatus {
    protected boolean focus;
    protected boolean trigger;

    public CameraStatus(CameraStatus other) {
        this.focus = other.focus;
        this.trigger = other.trigger;
    }
}

package de.zebrajaeger.phserver.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CameraFovSettings implements SettingsValue<CameraFovSettings> {
    private Double x;
    private Double y;

    @Override
    public void read(CameraFovSettings value) {
        value.setX(x);
        value.setY(y);
    }

    public void read(PanoFovSettings value) {
        value.getX().setFrom(0d);
        value.getX().setTo(x);
        value.getY().setFrom(0d);
        value.getY().setTo(y);
    }

    @Override
    public void write(CameraFovSettings value) {
        x = value.getX();
        y = value.getY();
    }

    public void write(PanoFovSettings value) {
        x = Math.abs(value.getX().getSize());
        y = Math.abs(value.getY().getSize());
    }
}

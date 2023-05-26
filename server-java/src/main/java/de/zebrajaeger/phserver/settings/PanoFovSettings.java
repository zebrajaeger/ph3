package de.zebrajaeger.phserver.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PanoFovSettings implements SettingsValue<PanoFovSettings> {

    private RangeSettings x = new RangeSettings();
    private RangeSettings y = new RangeSettings();
    private boolean fullX = false;
    private boolean fullY = false;

    @Override
    public void read(PanoFovSettings value) {
        x.read(value.getX());
        y.read(value.getY());
        value.setFullX(fullX);
        value.setFullY(fullY);
    }

    @Override
    public void write(PanoFovSettings value) {
        value.getX().read(x);
        value.getY().read(y);
        fullX = value.isFullX();
        fullY = value.isFullY();
    }

    public PanoFovSettings normalize() {
        return new PanoFovSettings(
                getX() == null ? null : getX().normalize(),
                getY() == null ? null : getY().normalize(),
                fullX, fullY);
    }

    @JsonIgnore
    public boolean isComplete() {
        return (fullX ||x.isComplete()) && (fullY || y.isComplete());
    }
}

package de.zebrajaeger.phserver.settings;

import de.zebrajaeger.phserver.data.Pattern;
import lombok.Data;

@Data
public class PanoSettings implements SettingsValue<PanoSettings> {

    private Pattern pattern = Pattern.GRID;

    @Override
    public void read(PanoSettings value) {
        value.setPattern(pattern);
    }

    @Override
    public void write(PanoSettings value) {
        pattern = value.getPattern();
    }
}

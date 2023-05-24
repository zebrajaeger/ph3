package de.zebrajaeger.phserver.papywizard.settings;

import de.zebrajaeger.phserver.data.Pattern;
import lombok.Data;

@Data
public class PanoSettings {

    private Pattern pattern = Pattern.GRID;
}

package de.zebrajaeger.phserver.papywizard;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Header {
    public General general = new General();
    public Shooting shooting = new Shooting();
    public Camera camera = new Camera();
    public Lens lens = new Lens();
    public Preset preset = new Preset();
}

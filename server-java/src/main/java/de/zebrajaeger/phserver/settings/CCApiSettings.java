package de.zebrajaeger.phserver.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CCApiSettings implements SettingsValue<CCApiSettings> {
    private String url;

    @Override
    public void read(CCApiSettings value) {
        value.setUrl(url);
    }

    @Override
    public void write(CCApiSettings value) {
        url = value.getUrl();
    }
}

package de.zebrajaeger.phserver.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RangeSettings implements SettingsValue<RangeSettings> {

    private Double from;
    private Double to;

    @Override
    public void read(RangeSettings value) {
        value.setFrom(from);
        value.setTo(to);
    }

    @Override
    public void write(RangeSettings value) {
        from = value.getFrom();
        to = value.getTo();
    }

    @JsonIgnore
    public boolean isComplete() {
        return from != null && to != null;
    }

    public RangeSettings normalize() {
        return (isComplete() && from > to) ? new RangeSettings(to, from) : new RangeSettings(from, to);
    }

    @JsonIgnore
    public Double getSize() {
        return isComplete() ? to - from : null;
    }
}

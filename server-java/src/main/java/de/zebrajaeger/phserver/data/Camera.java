package de.zebrajaeger.phserver.data;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

@Data
@NoArgsConstructor
public class Camera {
    protected boolean focus;
    protected boolean trigger;

    public Camera(Camera other) {
        this.focus = other.focus;
        this.trigger = other.trigger;
    }
}

package de.zebrajaeger.phserver.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PanoMatrixPosition {
    private Integer id;
    private Double x;
    private Double y;

    public PanoMatrixPosition(Double x){
        this.x = x;
    }
}

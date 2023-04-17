package de.zebrajaeger.phserver.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Image {

  private double width;
  private double height;

  public Image normalized() {
    return new Image(Math.abs(width), Math.abs(height));
  }

  public boolean isComplete() {
    return width != 0d && height != 0d;
  }
}

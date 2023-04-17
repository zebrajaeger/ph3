package de.zebrajaeger.phserver.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RawPosition {

  private final int x;
  private final int y;
}

import {Pipe, PipeTransform} from '@angular/core';
import {Position} from "../../data/panohead";

@Pipe({
  name: 'position'
})
export class PositionPipe implements PipeTransform {
  transform(position: Position | undefined | null, digits = 2): unknown {
    if (position == null || position.x == null || position.y == null) {
      return '-, -';
    } else {
      return `${position.x.toFixed(digits)}, ${position.y.toFixed(digits)}`;
    }
  }
}

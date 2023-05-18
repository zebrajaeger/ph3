import {Pipe, PipeTransform} from '@angular/core';
import {FieldOfViewPartial, Range} from "../../data/pano";

@Pipe({
  name: 'fovPartialSize'
})
export class FovPartialSizePipe implements PipeTransform {
  transform(fov: FieldOfViewPartial | undefined | null, ...args: unknown[]): unknown {
    if (fov == null) {
      return '-, -';
    } else {

      let x: string;
      if (fov.fullX) {
        x = '֍';
      } else {
        x = `${FovPartialSizePipe.rangeToString(fov.horizontal)}`;
      }
      let y: string;
      if (fov.fullY) {
        y = '֍';
      } else {
        y = `${FovPartialSizePipe.rangeToString(fov.vertical)}`;
      }

      return `${x}, ${y}`;
    }
  }

  private static rangeToString(range: Range): string {
    if (range == null || range.from == null || range.to == null) {
      return '-'
    }

    const l = Math.abs(range.to - range.from);
    return l.toFixed(2);
  }
}

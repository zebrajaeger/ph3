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
      if (fov.partial) {
        return `${FovPartialSizePipe.rangeToString(fov.horizontal)}, ${FovPartialSizePipe.rangeToString(fov.vertical)}`;
      } else {
        return `֍, ${FovPartialSizePipe.rangeToString(fov.vertical)}`;
      }
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

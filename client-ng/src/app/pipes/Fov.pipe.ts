import {Pipe, PipeTransform} from '@angular/core';
import {CameraOfView, Range} from "../../data/pano";

@Pipe({
  name: 'fovSize'
})
export class FovSizePipe implements PipeTransform {
  transform(fov: CameraOfView | undefined | null, ...args: unknown[]): unknown {
    if (fov == null) {
      return '-, -';
    } else {
      return `${FovSizePipe.rangeToString(fov.x)}, ${FovSizePipe.rangeToString(fov.y)}`;
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

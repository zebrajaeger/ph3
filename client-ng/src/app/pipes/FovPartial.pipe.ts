import {Pipe, PipeTransform} from '@angular/core';
import {PanoFieldOfView, Range} from "../../data/pano";

@Pipe({
  name: 'fovPartialSize'
})
export class FovPartialSizePipe implements PipeTransform {


  private infinity:string;
  constructor() {
    this.infinity =  '<span class="icon-all_inclusive"></span>';
  }

  transform(fov: PanoFieldOfView | undefined | null, ...args: unknown[]): unknown {
    if (fov == null) {
      return '-, -';
    } else {

      let x: string;
      if (fov.fullX) {
        x = 'B';
      } else {
        x = `${FovPartialSizePipe.rangeToString(fov.x)}`;
      }
      let y: string;
      if (fov.fullY) {
        y = 'B';
      } else {
        y = `${FovPartialSizePipe.rangeToString(fov.y)}`;
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

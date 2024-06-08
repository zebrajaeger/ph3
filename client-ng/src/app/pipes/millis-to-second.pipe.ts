import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'millisToSecond'
})
export class MillisToSecondPipe implements PipeTransform {

  transform(value: number|undefined): number|undefined {
    if (value === null || value === undefined) {
      return undefined;
    } else {
      return (value)/1000;
    }
  }
}

import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
    name: 'msToSecond'
})
export class MsToSecondPipe implements PipeTransform {
    transform(value: number, ...args: unknown[]): unknown {
        if (value === null || value === undefined) {
            return '-';
        } else {
            return (value / 1000).toFixed(3);
        }
    }
}

import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
    name: 'formatSecond'
})
export class FormatSecondPipe implements PipeTransform {
    transform(value: number, digits = 3): unknown {
        if (value === null || value === undefined) {
            return '-';
        } else {
            return (value).toFixed(digits);
        }
    }
}

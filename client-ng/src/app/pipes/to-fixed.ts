import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
    name: 'toFixed'
})
export class ToFixed implements PipeTransform {
    transform(value: number, digits = 2): unknown {
        if (value === null || value === undefined) {
            return '-';
        } else {
            return (value).toFixed(digits);
        }
    }
}

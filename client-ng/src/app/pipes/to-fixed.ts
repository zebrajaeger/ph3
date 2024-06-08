import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
    name: 'toFixed'
})
export class ToFixed implements PipeTransform {
    transform(value: number|undefined, digits = 2): string {
        if (value === null || value === undefined) {
            return '-';
        } else {
            return (value).toFixed(digits);
        }
    }
}

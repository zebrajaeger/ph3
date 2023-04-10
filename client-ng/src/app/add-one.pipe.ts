import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
    name: 'addOne'
})
export class AddOnePipe implements PipeTransform {
    transform(value: number|undefined|null, ...args: unknown[]): unknown {
        if (value === null || value === undefined) {
            return '-';
        } else {
            return value + 1;
        }
    }
}

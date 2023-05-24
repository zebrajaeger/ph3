import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
    selector: 'checkbox',
    templateUrl: './checkbox.component.html',
    styleUrls: ['./checkbox.component.scss']
})
export class CheckboxComponent {
    static idCounter = 0;

    id: string = `checkbox-${CheckboxComponent.idCounter++}}`;
    @Input()
    checked!: boolean;
    @Input()
    group!: string;
    @Input()
    value!: string;
    @Input()
    label!: string;
    @Output()
    click = new EventEmitter<string>();

    onClick($event: MouseEvent) {
        this.click.emit(this.value);
        $event.preventDefault();
    }
}

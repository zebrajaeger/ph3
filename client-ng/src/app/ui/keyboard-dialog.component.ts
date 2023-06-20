import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
    selector: 'app-keyboard-dialog',
    templateUrl: './keyboard-dialog.component.html',
    styleUrls: ['./keyboard-dialog.component.scss'],
})
export class KeyboardDialogComponent {
    public value_: string = ''
    @Output() public valueChange = new EventEmitter<string>();
    @Output() public onOk = new EventEmitter<string>();
    @Output() public onCancel = new EventEmitter<void>();

    public placeholder!: string;
    public isVisible: boolean = false;

    public isOkAvailable: boolean = false;

    @Input()
    public set value(value: string) {
        this.value_ = value;
        this.isOkAvailable = value.length > 0;
    }

    public get value() {
        return this.value_;
    }

    public show(placeholder: string, value: string) {
        this.placeholder = placeholder || '';
        this.value = value;
        this.isVisible = true;
    }

    _onOk() {
        if (!this.isOkAvailable) {
            return;
        }
        this.onOk.emit(this.value);
        this.isVisible = false;
    }

    _onCancel() {
        this.onCancel.emit();
        this.isVisible = false;
    }
}

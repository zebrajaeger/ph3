import {AfterViewInit, Component, EventEmitter, Input, Output} from '@angular/core';
import Keyboard from "simple-keyboard";
import {layouts} from "../utils";

@Component({
    selector: 'app-keyboard-dialog',
    templateUrl: './keyboard-dialog.component.html',
    styleUrls: ['./keyboard-dialog.component.scss'],
    host: {'class': 'modal'}
})
export class KeyboardDialogComponent implements AfterViewInit {
    @Input() public placeholder!: string;
    @Input() public value: string = ''
    @Output() public valueChange = new EventEmitter<string>();
    @Output() public onOk = new EventEmitter<void>();
    @Output() public onCancel = new EventEmitter<void>();

    keyboard!: Keyboard;

    ngAfterViewInit() {
        this.keyboard = new Keyboard({
            onChange: input => this._onChange(input),
            onKeyPress: button => this._onKeyPress(button),
            layout: layouts.de
        });
    }

    _onChange = (input: string) => {
        this.value = input;
        this.valueChange.emit(input);
    };

    _onKeyPress = (button: string) => {
        if (button === "{shift}" || button === "{lock}") this.handleShift();
    };

    _onInputChange = (event: any) => {
        this.keyboard.setInput(event.target.value);
    };

    handleShift = () => {
        let currentLayout = this.keyboard.options.layoutName;
        let shiftToggle = currentLayout === "default" ? "shift" : "default";

        this.keyboard.setOptions({
            layoutName: shiftToggle
        });
    };

    _onOk() {
        this.onOk.emit();
    }

    _onCancel() {
        this.onCancel.emit();
    }
}

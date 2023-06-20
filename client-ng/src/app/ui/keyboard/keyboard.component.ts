import {AfterViewInit, Component, EventEmitter, Input, Output} from '@angular/core';
import Keyboard from "simple-keyboard";
import {layouts} from "../../utils";

@Component({
  selector: 'app-keyboard',
  templateUrl: './keyboard.component.html',
  styleUrls: ['./keyboard.component.scss']
})
export class KeyboardComponent  implements AfterViewInit{
  @Output() public valueChange = new EventEmitter<string>();
  @Input()  public value: string = ''
  private keyboard!: Keyboard;

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

  handleShift = () => {
    let currentLayout = this.keyboard.options.layoutName;
    let shiftToggle = currentLayout === "default" ? "shift" : "default";

    this.keyboard.setOptions({
      layoutName: shiftToggle
    });
  };
}

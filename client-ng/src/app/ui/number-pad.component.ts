import { Component, EventEmitter, HostListener, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-number-pad',
  templateUrl: './number-pad.component.html',
  styleUrls: ['./number-pad.component.scss']
})
export class NumberPadComponent {

  @Input()
  private max = Math.pow(10, 6);

  @Input()
  private fraction = 2;

  public valueString: string | undefined;
  private _value = 0;
  @Output()
  private valueChange = new EventEmitter<number>();
  @Output()
  private close = new EventEmitter<void>();
  
  constructor() {
  }

  get value(): number {
    return this._value;
  }

  @Input()
  set value(value: number) {
    const v = Math.floor(value * this.divider);
    if (this._value === v) {
      return;
    }
    this.setValueInternal(v);
  }

  get divider(): number {
    return Math.pow(10, this.fraction);
  }

  private setValueInternal(value: number): void {
    this._value = value;

    const divider = this.divider;
    this.valueChange.emit(this._value / divider);
    this.valueString = (this._value / divider).toFixed(this.fraction);
  }

  onNumber(digit: number): void {
    console.log('onNumber', digit)
    const newValue = this._value * 10 + digit;
    if (newValue < this.max) {
      this.setValueInternal(this._value * 10 + digit);
    }
  }

  onDelete(): void {
    console.log('onDelete')
    this.setValueInternal(Math.floor(this._value / 10));
  }

  onClear(): void {
    this.setValueInternal(0);
  }

  @HostListener('window:keydown', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent) {
    const key = event.key;
    if (key >= '0' && key <= '9') {
      this.onNumber(parseInt(key, 10));
      // } else if (key === '.' || key === ',') {
      //   this.appendDecimal();
    } else if (key === 'Backspace') {
      this.onDelete();
    } else if (key === 'Escape') {
      this.onClear();
    } else if (key === 'Enter') {
      this.close.emit(); 
    }
  }
}

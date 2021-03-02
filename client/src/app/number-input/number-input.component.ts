import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';

export interface DialogData {
  value: number;
}

@Component({
  selector: 'app-number-input',
  templateUrl: './number-input.component.html',
  styleUrls: ['./number-input.component.scss']
})
export class NumberInputComponent {
  _digits = [0, 0, 0, 0, 0, 0, 0];

  constructor(
    public dialogRef: MatDialogRef<NumberInputComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData) {
    this.digits = data.value;
  }

  set digits(value: number) {
    if (value !== undefined) {
      const x = value.toFixed(2).toString().split('.');
      const x1 = x[0].padStart(5, '0');
      const x2 = x[1].padEnd(2, '0');

      for (let i = 0; i < 5; ++i) {
        this._digits[i] = parseInt(x1[i], 10);
      }
      this._digits[5] = parseInt(x2[0], 10);
      this._digits[6] = parseInt(x2[1], 10);
    } else {
      for (let i = 0; i < 7; ++i) {
        this._digits[i] = 0;
      }
    }
  }

  get digits(): number {
    let n = '';
    for (let i = 0; i < 5; ++i) {
      n += this._digits[i].toString();
    }
    n = n + '.' + this._digits[5].toString() + this._digits[6].toString();
    return parseFloat(n);
  }

  onNoClick(): void {
    this.dialogRef.close(undefined);
  }

  onYesClick(): void {
    this.dialogRef.close({value: this.digits});
  }

  onDigit(digit: number): void {
    this._digits = this._digits.slice(1).concat([digit]);
  }

  onDelete(): void {
    this._digits = [0].concat(this._digits.slice(0, -1));
  }

  onClear(): void {
    this._digits = [0, 0, 0, 0, 0, 0, 0];
  }
}

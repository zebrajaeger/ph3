import {Component, Input, EventEmitter, OnInit, Output} from '@angular/core';

export enum Border {
  Top,
  Right,
  Bottom,
  Left
}

@Component({
  selector: 'app-border-chooser',
  templateUrl: './border-chooser.component.html',
  styleUrls: ['./border-chooser.component.scss']
})
export class BorderChooserComponent {

  private _hasFull = false;
  @Output() hasFullChanged = new EventEmitter<boolean>();

  private _partial = false;
  @Output() partialChange = new EventEmitter<boolean>();

  @Output() border = new EventEmitter<Border>();
  @Input() topLabel = '';
  @Output() topChange = new EventEmitter<void>();
  @Input() rightLabel = '';
  @Output() rightChange = new EventEmitter<void>();
  @Input() bottomLabel = '';
  @Output() bottomChange = new EventEmitter<void>();
  @Input() leftLabel = '';
  @Output() leftChange = new EventEmitter<void>();

  get hasFull(): boolean {
    return this._hasFull;
  }

  @Input()
  set hasFull(value: boolean) {
    this._hasFull = value;
    this.hasFullChanged.emit(value);
  }

  get partial(): boolean {
    return this._partial;
  }

  @Input()
  set partial(partial: boolean) {
    console.log('SP', partial);
    this._partial = partial;
    this.partialChange.emit(partial);
  }

  onTopClick(): void {
    this.border.emit(Border.Top);
    this.topChange.emit();
  }

  onLeftClick(): void {
    this.border.emit(Border.Left);
    this.leftChange.emit();
  }

  onRightClick(): void {
    this.border.emit(Border.Right);
    this.rightChange.emit();
  }

  onBottomClick(): void {
    this.border.emit(Border.Bottom);
    this.bottomChange.emit();
  }
}

import {Component, Input, Output, EventEmitter} from '@angular/core';

@Component({
  selector: 'app-button',
  templateUrl: './button.component.html',
  styleUrls: ['./button.component.scss']
})
export class ButtonComponent {
  @Input()
  label = 'Button';
  @Input()
  disabled = false;
  @Output()
  clickChanged = new EventEmitter();

  _animated?: boolean;

  constructor() {
  }

  onClick(): void {
    if (!this.disabled) {
      this._animated = !this._animated;
      this.clickChanged.emit();
    }
  }
}

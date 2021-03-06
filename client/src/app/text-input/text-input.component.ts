import {Component, Input, OnInit, EventEmitter, Output, ChangeDetectionStrategy} from '@angular/core';

@Component({
  selector: 'app-text-input',
  templateUrl: './text-input.component.html',
  styleUrls: ['./text-input.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TextInputComponent implements OnInit {

  @Input() label = '';
  @Input() placeholder = '';
  @Input() text = '';
  @Output() textChange = new EventEmitter<string>();

  constructor() {
  }

  ngOnInit(): void {
  }

  onChar(q: string): void {
    this.text += q;
    this.textChange.emit(this.text);
  }

  onBackspace(): void {
    this.text = this.text.slice(0, -1);
    this.textChange.emit(this.text);
  }
}

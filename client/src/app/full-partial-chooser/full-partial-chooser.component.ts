import {EventEmitter, Component, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-full-partial-chooser',
  templateUrl: './full-partial-chooser.component.html',
  styleUrls: ['./full-partial-chooser.component.scss']
})
export class FullPartialChooserComponent {

  @Input() partial = false;
  @Output() partialChange = new EventEmitter<boolean>();

  constructor() {
  }

  onClick(): void {
    this.partial = !this.partial;
    this.partialChange.emit(this.partial);
  }
}

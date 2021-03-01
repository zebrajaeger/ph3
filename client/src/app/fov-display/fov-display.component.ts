import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-fov-display',
  templateUrl: './fov-display.component.html',
  styleUrls: ['./fov-display.component.scss']
})
export class FovDisplayComponent {
  @Input()
  w?: number;
  @Input()
  h?: number;
}

import {Component, Input} from '@angular/core';

@Component({
  selector: 'led',
  templateUrl: './led.component.html',
  styleUrls: ['./led.component.scss']
})
export class LedComponent {
  @Input()
  public width: number = 20;

  @Input()
  public height: number = 20;

  @Input()
  public color: string = "green";
}

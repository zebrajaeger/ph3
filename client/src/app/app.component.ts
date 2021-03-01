import {Component} from '@angular/core';
import {StateService} from './state.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'client';

  constructor(private stateService: StateService) {

  }

  onRouterOutletActivate($event: any): void {
    this.stateService.currentRouterComponent = $event;
  }
}

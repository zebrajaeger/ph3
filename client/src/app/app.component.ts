import {Component} from '@angular/core';
import {RouterService} from './router.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'client';

  constructor(private routerService: RouterService) {

  }

  onRouterOutletActivate($event: any): void {
    this.routerService.onRouteChange($event);
  }
}


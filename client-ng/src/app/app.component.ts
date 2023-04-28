import {Component, OnDestroy, OnInit} from '@angular/core';
import {RouterService} from './service/router.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy {

  constructor(private routerService: RouterService) {
  }

  ngOnInit(): void {
  }

  ngOnDestroy(): void {
  }

  onRouterOutletActivate($event: any): void {
    this.routerService.onRouteChange($event);
  }
}

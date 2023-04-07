import {Component, OnDestroy, OnInit} from '@angular/core';
import {RouterService} from './router.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy {
  // public panoHeadData: Actor;
  // public panoHeadDataSubscription: Subscription;

  constructor(private routerService: RouterService) {
  }

  ngOnInit(): void {
    // this.panoHeadDataSubscription = this.panoHeadService.subscribeActor(panoHeadData => this.panoHeadData = panoHeadData);
  }

  ngOnDestroy(): void {
    // this.panoHeadDataSubscription?.unsubscribe();
  }

  onRouterOutletActivate($event: any): void {
    this.routerService.onRouteChange($event);
  }
}

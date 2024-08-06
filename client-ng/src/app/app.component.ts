import { Component, OnInit } from '@angular/core';
import { RouterService } from './service/router.service';
import { GpsService } from './service/gps.service';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

    constructor(private routerService: RouterService, private gpsService: GpsService) {
    }

    onRouterOutletActivate($event: any): void {
        this.routerService.onRouteChange($event);
    }

    ngOnInit() {
        this.gpsService.startTracking();
    }
}

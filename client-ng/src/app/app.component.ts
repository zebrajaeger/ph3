import {Component, OnDestroy} from '@angular/core';
import {Subscription} from 'rxjs';
import {PanoHeadService} from './panohead.service';
import {Actor} from '../data/panohead';
import {RouterService} from './router.service';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnDestroy {
    public panoHeadData: Actor;
    public panoHeadDataSubscription: Subscription;

    constructor(private routerService: RouterService, private panoHeadService: PanoHeadService) {
        this.panoHeadDataSubscription =
            this.panoHeadService
                .actor()
                .subscribe(panoHeadData => this.panoHeadData = panoHeadData);
    }

    ngOnDestroy(): void {
        if (this.panoHeadDataSubscription) {
            this.panoHeadDataSubscription.unsubscribe();
        }
    }

    onRouterOutletActivate($event: any): void {
        this.routerService.onRouteChange($event);
    }
}

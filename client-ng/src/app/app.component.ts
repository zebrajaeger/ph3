import {Component, OnDestroy} from '@angular/core';
import {Subscription} from 'rxjs';
import {PanoHeadService} from './panohead.service';
import {Actor} from '../data/panohead';
import {RouterService} from './router.service';
import {OnInit} from '@angular/core/core';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy {
    // public panoHeadData: Actor;
    // public panoHeadDataSubscription: Subscription;

    constructor(private routerService: RouterService,
                private panoHeadService: PanoHeadService) {
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

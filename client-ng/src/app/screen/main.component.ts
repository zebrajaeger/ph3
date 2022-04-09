import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {UiService} from '../ui.service';
import {RouterService} from '../router.service';
import {PanoHeadService} from '../panohead.service';

@Component({
    selector: 'app-main',
    templateUrl: './main.component.html',
    styleUrls: ['./main.component.scss']
})
export class MainComponent {
    constructor(public router: Router,
                private routerService: RouterService,
                private panoHeadService: PanoHeadService,
                private uiService: UiService) {
        this.routerService.onActivate(this, () => this.onActivate());
    }

    private onActivate(): void {
        this.uiService.title.next('Panohead');
        this.uiService.backButton.next(false);
        this.panoHeadService.sendJogging(false);
    }
}

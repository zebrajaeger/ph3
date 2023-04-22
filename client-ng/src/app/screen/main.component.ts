import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {UiService} from '../service/ui.service';
import {RouterService} from '../service/router.service';
import {PanoHeadService} from '../service/panohead.service';
import {PanoService} from '../service/pano.service';

@Component({
    selector: 'app-main',
    templateUrl: './main.component.html',
    styleUrls: ['./main.component.scss']
})
export class MainComponent {
    constructor(public router: Router,
                private routerService: RouterService,
                private panoService: PanoService,
                private panoHeadService: PanoHeadService,
                private uiService: UiService) {
        this.routerService.onActivate(this, () => this.onActivate());
    }

    private onActivate(): void {
        this.uiService.title.next('Panohead');
        this.uiService.backButton.next(false);
        this.panoHeadService.sendJogging(false);
        this.panoService.requestRecalculatePano();
    }
}

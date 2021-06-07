import {Component, OnInit} from '@angular/core';
import {RouterService} from '../router.service';
import {UiService} from '../ui.service';
import {PanoHeadService} from '../panohead.service';

@Component({
    selector: 'app-record',
    templateUrl: './record.component.html',
    styleUrls: ['./record.component.scss']
})
export class RecordComponent implements OnInit {

    constructor(private routerService: RouterService,
                private panoHeadService: PanoHeadService,
                private uiService: UiService) {
        routerService.onActivate(this, () => this.onActivate());
    }

    ngOnInit(): void {

    }

    onStart(): void {
        this.panoHeadService.sendStartRecord();
    }

    onStop(): void {
        this.panoHeadService.sendStopRecord();
    }

    onPause(): void {
        this.panoHeadService.sendPauseResumeRecord();
    }

    private onActivate(): void {
        this.uiService.title.next('Record');
        this.uiService.backButton.next(true);
    }
}

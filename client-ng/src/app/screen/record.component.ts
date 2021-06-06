import {Component, OnInit} from '@angular/core';
import {PanoService} from '../pano.service';
import {CameraService} from '../camera.service';
import {RouterService} from '../router.service';
import {UiService} from '../ui.service';

@Component({
    selector: 'app-record',
    templateUrl: './record.component.html',
    styleUrls: ['./record.component.scss']
})
export class RecordComponent implements OnInit {

    constructor(private routerService: RouterService, private uiService: UiService) {
        routerService.onActivate(this, () => this.onActivate());
    }

    ngOnInit(): void {
    }

    onStart(): void {
    }

    onStop(): void {

    }

    onPause(): void {

    }

    private onActivate(): void {
        this.uiService.title.next('Record');
        this.uiService.backButton.next(true);
    }
}

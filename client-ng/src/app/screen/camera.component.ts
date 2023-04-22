import {Component, OnDestroy, OnInit} from '@angular/core';
import {CameraService} from '../service/camera.service';
import {RouterService} from '../service/router.service';
import {UiService} from '../service/ui.service';
import {PanoService} from '../service/pano.service';

@Component({
    selector: 'app-camera',
    templateUrl: './camera.component.html',
    styleUrls: ['./camera.component.scss']
})
export class CameraComponent implements OnInit, OnDestroy {

    constructor(private cameraService: CameraService,
                private routerService: RouterService,
                private panoService: PanoService,
                private uiService: UiService) {
        routerService.onActivate(this, () => this.onActivate());
    }

    ngOnInit(): void {
    }

    ngOnDestroy(): void {
    }

    onFocus(): void {
        this.cameraService.focus(1000);
    }

    onTrigger(): void {
        this.cameraService.trigger(1000);
    }

    onShot(): void {
        this.cameraService.shot(1000, 500);
    }

    private onActivate(): void {
        this.uiService.title.next('Camera');
        this.uiService.backButton.next(true);
        this.panoService.requestRecalculatePano();
    }
}

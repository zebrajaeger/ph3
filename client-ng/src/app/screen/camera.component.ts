import {Component, OnDestroy, OnInit} from '@angular/core';
import {CameraService} from '../camera.service';
import {RouterService} from '../router.service';
import {UiService} from '../ui.service';

@Component({
    selector: 'app-camera',
    templateUrl: './camera.component.html',
    styleUrls: ['./camera.component.scss']
})
export class CameraComponent implements OnInit, OnDestroy {

    constructor(private cameraService: CameraService, private routerService: RouterService, private uiService: UiService) {
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
        this.cameraService.shot(1000, 100);
    }

    private onActivate(): void {
        this.uiService.title.next('Camera');
        this.uiService.backButton.next(true);
    }
}

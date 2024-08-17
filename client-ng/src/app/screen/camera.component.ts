import { Component } from '@angular/core';
import { CameraService } from '../service/camera.service';
import { RouterService } from '../service/router.service';
import { UiService } from '../service/ui.service';
import { PanoService } from '../service/pano.service';

@Component({
    selector: 'app-camera',
    templateUrl: './camera.component.html',
    styleUrls: ['./camera.component.scss']
})
export class CameraComponent {
    public statusText:String='';
    constructor(private cameraService: CameraService,
        private routerService: RouterService,
        private panoService: PanoService,
        private uiService: UiService) {
        routerService.onActivate(this, () => this.onActivate());
    }

    onFocus(): void {
        this.cameraService.focus(1000);
        this.statusText = '';
    }

    onTrigger(): void {
        this.cameraService.trigger(1000);
        this.statusText = '';
    }

    onShot(): void {
        this.statusText = 'Request shot';
        this.cameraService.requestShot(csr => {
            if(!csr.successfully){
                this.statusText = csr.message;
                console.log(csr)
            } else {
                this.statusText = 'Shot successfully requested';
            }
        });
    }

    private onActivate(): void {
        this.uiService.title.next('Camera');
        this.uiService.backButton.next(true);
        this.panoService.requestRecalculatePano();
    }
}

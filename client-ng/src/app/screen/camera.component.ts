import { Component, OnDestroy, ViewChild } from '@angular/core';
import { CameraService } from '../service/camera.service';
import { RouterService } from '../service/router.service';
import { UiService } from '../service/ui.service';
import { PanoService } from '../service/pano.service';
import { Subscription } from 'rxjs';
import { KeyboardDialogComponent } from '../ui/keyboard-dialog.component';
import { CCApi } from 'src/data/camera';

@Component({
    selector: 'app-camera',
    templateUrl: './camera.component.html',
    styleUrls: ['./camera.component.scss']
})
export class CameraComponent implements OnDestroy {
    public statusText: string = '';
    public ccapi?: CCApi;
    private ccapiUrlSubscription?: Subscription;
    @ViewChild('keyboard')
    private keyboardDialog!: KeyboardDialogComponent;

    constructor(private cameraService: CameraService,
        private routerService: RouterService,
        private panoService: PanoService,
        private uiService: UiService) {
        routerService.onActivate(this, () => this.onActivate());
        cameraService.requestCCApiUrl(ccapi => this.ccapi = ccapi);
        this.ccapiUrlSubscription = cameraService.subscribeCCApiUrl(ccapi => {
            this.ccapi = ccapi;
        });
    }

    ngOnDestroy(): void {
        this.ccapiUrlSubscription?.unsubscribe();
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
            if (!csr.successfully) {
                this.statusText = csr.message;
                console.log(csr)
            } else {
                this.statusText = 'Shot successfully requested';
            }
        });
    }

    onEditUrl(): void {
        this.keyboardDialog.show('CCAPi url like http://192.168.8.149:8080/ccapi', this.ccapi ? this.ccapi.url : '');
    }

    onKeyboardOk(url: string) {
        const ccapi = new CCApi();
        ccapi.url = url;
        this.cameraService.setCCApiUrl(ccapi);
    }

    private onActivate(): void {
        this.uiService.title.next('Camera');
        this.uiService.backButton.next(true);
        this.panoService.requestRecalculatePano();
    }
}

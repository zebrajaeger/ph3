import {Component, OnDestroy, OnInit} from '@angular/core';
import {PanoService} from '../../service/pano.service';
import {Border, CameraOfView} from '../../../data/pano';
import {Subscription} from 'rxjs';
import {RouterService} from '../../service/router.service';
import {UiService} from '../../service/ui.service';
import {PanoHeadService} from '../../service/panohead.service';
import {ConnectionService} from '../../service/connection.service';
import {degToString} from '../../utils';
import {ModalService} from "../../ui/modal.service";

@Component({
    selector: 'app-picture-fov',
    templateUrl: './picture-fov.component.html',
    styleUrls: ['./picture-fov.component.scss']
})
export class PictureFovComponent implements OnInit, OnDestroy {
    private openSubscription!: Subscription;

    public fov_!: CameraOfView;
    private fovSubscription!: Subscription;

    public hFromText?: string;
    public hToText?: string;
    public hText?: string;

    public vText?: string;
    public vFromText?: string;
    public vToText?: string;

    constructor(private connectionService: ConnectionService,
                private panoService: PanoService,
                private panoHeadService: PanoHeadService,
                private routerService: RouterService,
                public modalService: ModalService,
                private uiService: UiService) {
        this.routerService.onActivate(this, () => this.onActivate());
    }

    ngOnInit(): void {
        this.openSubscription = this.connectionService.subscribeOpen(() => this.onActivate());
        this.fovSubscription = this.panoService.subscribePictureFov(fov => this.fov = fov);
    }

    ngOnDestroy(): void {
        this.openSubscription?.unsubscribe();
        this.fovSubscription?.unsubscribe();
    }

    set fov(fov: CameraOfView) {
        this.fov_ = fov;
        if (fov.x.size != undefined) {
            this.hText = degToString(Math.abs(fov.x.size));
        }
        this.hFromText = degToString(fov.x.from);
        this.hToText = degToString(fov.x.to);
        if (fov.y.size != undefined) {
            this.vText = degToString(Math.abs(fov.y.size));
        }
        this.vFromText = degToString(fov.y.from);
        this.vToText = degToString(fov.y.to);
    }

    onTop(): void {
        this.panoService.setPictureBorder(Border.TOP);
    }

    onLeft(): void {
        this.panoService.setPictureBorder(Border.LEFT);
    }

    onRight(): void {
        this.panoService.setPictureBorder(Border.RIGHT);
    }

    onBottom(): void {
        this.panoService.setPictureBorder(Border.BOTTOM);
    }

    private onActivate(): void {
        this.uiService.title.next('Camera FOV');
        this.uiService.backButton.next(true);
        this.panoHeadService.sendJogging(true);
        this.panoService.requestPictureFov(fov => this.fov = fov);
        this.panoService.requestRecalculatePano();
    }

    onClosePopup() {
        this.modalService.close('picture-fov-set');
    }
}

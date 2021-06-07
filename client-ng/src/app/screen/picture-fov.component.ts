import {Component, OnInit} from '@angular/core';
import {PanoService} from '../pano.service';
import {Border, FieldOfView, FieldOfViewPartial} from '../../data/pano';
import {Subscription} from 'rxjs';
import {RouterService} from '../router.service';
import {UiService} from '../ui.service';
import {PanoHeadService} from '../panohead.service';
import {ConnectionService} from '../connection.service';
import {OnDestroy} from '@angular/core/core';

@Component({
    selector: 'app-picture-fov',
    templateUrl: './picture-fov.component.html',
    styleUrls: ['./picture-fov.component.scss']
})
export class PictureFovComponent implements OnInit, OnDestroy {
    private openSubscription: Subscription;

    public fov_: FieldOfViewPartial;
    private fovSubscription: Subscription;

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
                private uiService: UiService
    ) {
    }

    ngOnInit(): void {
        this.routerService.onActivate(this, () => this.onActivate());
        this.openSubscription = this.connectionService.subscribeOpen(() => this.onActivate());
        this.fovSubscription = this.panoService.subscribePanoFov(fov => this.fov = fov);
    }

    ngOnDestroy(): void {
        this.openSubscription?.unsubscribe();
        this.fovSubscription?.unsubscribe();
    }

    set fov(fov: FieldOfViewPartial ) {
        this.fov_ = fov;
        this.hText = this.revToString(fov.horizontal.size);
        this.hFromText = this.revToString(fov.horizontal.from);
        this.hToText = this.revToString(fov.horizontal.to);
        this.vText = this.revToString(fov.vertical.size);
        this.vFromText = this.revToString(fov.vertical.from);
        this.vToText = this.revToString(fov.vertical.to);
    }

    private revToString(rev: number): string {
        if (rev) {
            const size = Math.abs(rev);
            return size.toFixed(3) + ' (' + (size * 360).toFixed(1) + 'deg)';
        }
        return '';
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
        this.panoHeadService.jogging(true);

        this.panoService.requestPanoFov(fov => this.fov = fov);
    }
}

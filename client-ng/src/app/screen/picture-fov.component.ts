import {Component, OnInit} from '@angular/core';
import {PanoService} from '../pano.service';
import {Border, FieldOfView} from '../../data/pano';
import {Subscription} from 'rxjs';
import {RouterService} from '../router.service';
import {UiService} from '../ui.service';
import {PanoHeadService} from '../panohead.service';

@Component({
    selector: 'app-picture-fov',
    templateUrl: './picture-fov.component.html',
    styleUrls: ['./picture-fov.component.scss']
})
export class PictureFovComponent implements OnInit {
    public fov_: FieldOfView;
    public fovSubscription: Subscription;

    public hFromText?: string;
    public hToText?: string;
    public hText?: string;

    public vText?: string;
    public vFromText?: string;
    public vToText?: string;

    constructor(private panoService: PanoService,
                private panoHeadService: PanoHeadService,
                private routerService: RouterService,
                private uiService: UiService) {
        routerService.onActivate(this, () => this.onActivate());

        this.fovSubscription =
            panoService
                .pictureFov()
                .subscribe(fov => this.fov = fov);
    }

    set fov(fov: FieldOfView) {
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

    ngOnInit(): void {
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
    }
}

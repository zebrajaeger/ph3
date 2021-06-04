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
    public fov: FieldOfView;
    public fovSubscription: Subscription;

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

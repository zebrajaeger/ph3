import {Component, OnInit} from '@angular/core';
import {Border, CalculatedPano, FieldOfViewPartial} from '../../data/pano';
import {Subscription} from 'rxjs';
import {PanoService} from '../pano.service';
import {PanoHeadService} from '../panohead.service';
import {RouterService} from '../router.service';
import {UiService} from '../ui.service';

@Component({
    selector: 'app-pano-fov',
    templateUrl: './pano-fov.component.html',
    styleUrls: ['./pano-fov.component.scss']
})
export class PanoFovComponent implements OnInit {
    public fov: FieldOfViewPartial;
    public calc: CalculatedPano;
    public fovSubscription: Subscription;

    constructor(private panoService: PanoService,
                private panoHeadService: PanoHeadService,
                private routerService: RouterService,
                private uiService: UiService) {
        routerService.onActivate(this, () => this.onActivate());

        this.fovSubscription =
            panoService
                .panoFov()
                .subscribe(fov => {
                    this.fov = fov;
                });

        panoService
            .calculatedPano()
            .subscribe(calc => {
                this.calc = calc;
            });
    }

    ngOnInit(): void {
    }

    onTop(): void {
        this.panoService.setPanoBorder(Border.TOP);
    }

    onLeft(): void {
        this.panoService.setPanoBorder(Border.LEFT);
    }

    onRight(): void {
        this.panoService.setPanoBorder(Border.RIGHT);
    }

    onBottom(): void {
        this.panoService.setPanoBorder(Border.BOTTOM);
    }

    onPartial(): void {
        this.panoService.setPanoPartial(!this.fov.partial);
    }

    private onActivate(): void {
        this.uiService.title.next('Pano FOV');
        this.uiService.backButton.next(true);
        this.panoHeadService.jogging(true);
    }
}

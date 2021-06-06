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
    private _fov_: FieldOfViewPartial;
    public calc: CalculatedPano;
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


    get fov(): FieldOfViewPartial {
        return this._fov_;
    }

    set fov(fov: FieldOfViewPartial) {
        this._fov_ = fov;
        this.hText = this.revToString(fov.horizontal.size);
        this.hFromText = this.revToString(fov.horizontal.from);
        this.hToText = this.revToString(fov.horizontal.to);
        this.vText = this.revToString(fov.vertical.size);
        this.vFromText = this.revToString(fov.vertical.from);
        this.vToText = this.revToString(fov.vertical.to);
    }

    ngOnInit(): void {
    }

    private revToString(rev: number): string {
        if (rev) {
            const size = Math.abs(rev);
            return size.toFixed(3) + ' (' + (size * 360).toFixed(1) + 'deg)';
        }
        return '';
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

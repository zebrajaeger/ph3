import {Component, OnInit} from '@angular/core';
import {Border, CalculatedPano, FieldOfViewPartial} from '../../data/pano';
import {Subscription} from 'rxjs';
import {PanoService} from '../pano.service';
import {PanoHeadService} from '../panohead.service';
import {RouterService} from '../router.service';
import {UiService} from '../ui.service';
import {OnDestroy} from '@angular/core/core';
import {ConnectionService} from '../connection.service';

@Component({
    selector: 'app-pano-fov',
    templateUrl: './pano-fov.component.html',
    styleUrls: ['./pano-fov.component.scss']
})
export class PanoFovComponent implements OnInit, OnDestroy {
    private _fov_: FieldOfViewPartial;
    public calc: CalculatedPano;
    private fovSubscription: Subscription;

    private openSubscription: Subscription;
    private calculatedPanoSubscription: Subscription;

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
                private uiService: UiService) {
        this.routerService.onActivate(this, () => this.onActivate());
    }

    ngOnInit(): void {
        this.openSubscription = this.connectionService.subscribeOpen(() => this.onActivate());
        this.fovSubscription = this.panoService.subscribePanoFov(fov => this.fov = fov);
        this.calculatedPanoSubscription = this.panoService.subscribeCalculatedPano(calculatedPano => this.calc = calculatedPano);
    }

    ngOnDestroy(): void {
        this.openSubscription?.unsubscribe();
        this.fovSubscription?.unsubscribe();
        this.calculatedPanoSubscription?.unsubscribe();
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

    private revToString(rev: number): string {
        if (rev === null || rev === undefined) {
            return '-';
        }
        const size = Math.abs(rev);
        return size.toFixed(3) + ' (' + (size * 360).toFixed(1) + 'deg)';
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
        this.panoHeadService.sendJogging(true);

        this.panoService.requestPanoFov(fov => this.fov = fov);
        this.panoService.requestCalculatedPano(calculatedPano => this.calc = calculatedPano);
    }
}

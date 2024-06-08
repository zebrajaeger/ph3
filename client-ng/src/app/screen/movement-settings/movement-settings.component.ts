import {Component} from '@angular/core';
import {Subscription} from "rxjs";
import {Delay} from "../../../data/pano";
import {UiService} from "../../service/ui.service";
import {ConnectionService} from "../../service/connection.service";
import {RouterService} from "../../service/router.service";
import {PanoService} from "../../service/pano.service";
import {ModalService} from "../../ui/modal.service";

@Component({
    selector: 'app-movement-settings',
    templateUrl: './movement-settings.component.html',
    styleUrls: ['./movement-settings.component.scss']
})
export class MovementSettingsComponent {
    private openSubscription!: Subscription;

    private delaySubscription!: Subscription;
    private _delay?: Delay;

    public delayAfterMove = 1.23;
    public delayBetweenShots: number = 0;
    public delayAfterShots: number = 0;

    constructor(private uiService: UiService,
                private connectionService: ConnectionService,
                private routerService: RouterService,
                private panoService: PanoService,
                public modalService: ModalService) {
        this.routerService.onActivate(this, () => this.onActivate());
    }

    ngOnInit(): void {
        this.openSubscription = this.connectionService.subscribeOpen(() => this.onActivate());
        this.delaySubscription = this.panoService.subscribeDelay((delay) => this.delay = delay);
        this.panoService.requestDelay((delay) => this.delay = delay);
    }

    ngOnDestroy(): void {
        this.openSubscription?.unsubscribe();
        this.delaySubscription?.unsubscribe();
    }

    get delay(): Delay | undefined {
        return this._delay;
    }

    set delay(value: Delay | undefined) {
        console.log('DELAY', value)
        this._delay = value;
        if (!value) {
            return;
        }
        this.delayAfterMove = value.waitAfterMove / 1000;
        this.delayBetweenShots = value.waitBetweenShots / 1000;
        this.delayAfterShots = value.waitAfterShot / 1000;
    }

    private onActivate(): void {
        this.uiService.title.next('Movement Settings');
        this.uiService.backButton.next(true);
        this.panoService.requestDelay(delay => this.delay = delay);
    }

    onDelayAfterMoveClose(): void {
        this.panoService.setDelayWaitAfterMoveMs(this.delayAfterMove * 1000);
    }

    onDelayBetweenShotsClose() {
        this.panoService.setDelayWaitBetweenShotsMs(this.delayBetweenShots * 1000);
    }

    onDelayAfterShotsClose() {
        this.panoService.setDelayWaitAfterShotMs(this.delayAfterShots * 1000);
    }
}

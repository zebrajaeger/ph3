import {Component, OnInit} from '@angular/core';
import {UiService} from '../ui.service';
import {Subscription} from 'rxjs';
import {ConnectionService} from '../connection.service';
import {RouterService} from '../router.service';
import {ModalService} from '../ui/modal.service';
import {OnDestroy} from '@angular/core/core';
import {PanoService} from '../pano.service';
import {Shots} from '../../data/camera';

@Component({
    selector: 'app-pano-settings',
    templateUrl: './pano-settings.component.html',
    styleUrls: ['./pano-settings.component.scss']
})
export class PanoSettingsComponent implements OnInit, OnDestroy {
    private openSubscription: Subscription;

    private shotSubscription: Subscription;
    public shots?: Shots = null;

    public focusTime = 1.23;
    public triggerTime = 1.23;
    public delayAfterMove = 1.23;

    constructor(private uiService: UiService,
                private connectionService: ConnectionService,
                private routerService: RouterService,
                private panoService: PanoService,
                public modalService: ModalService) {
        this.routerService.onActivate(this, () => this.onActivate());
    }

    ngOnInit(): void {
        this.openSubscription = this.connectionService.subscribeOpen(() => this.onActivate());

        this.shotSubscription = this.panoService.subscribeShots((shots) => this.shots = shots);
    }

    ngOnDestroy(): void {
        this.openSubscription?.unsubscribe();
    }

    private onActivate(): void {
        this.uiService.title.next('Pano Settings');
        this.uiService.backButton.next(true);
        this.panoService.requestShots(shots => this.shots = shots);
    }

    onFocusTime(value: number): void {
        this.focusTime = value;
    }
}

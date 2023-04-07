import {Component, OnDestroy, OnInit} from '@angular/core';
import {RouterService} from '../router.service';
import {UiService} from '../ui.service';
import {PanoHeadService} from '../panohead.service';
import {RecordState} from '../../data/record';
import {Subscription} from 'rxjs';
import {PanoService} from '../pano.service';

@Component({
    selector: 'app-record',
    templateUrl: './record.component.html',
    styleUrls: ['./record.component.scss']
})
export class RecordComponent implements OnInit, OnDestroy {
    private recordStateSubscription: Subscription;
    public _state: RecordState;

    constructor(private routerService: RouterService,
                private panoHeadService: PanoHeadService,
                private panoService: PanoService,
                private uiService: UiService) {
        routerService.onActivate(this, () => this.onActivate());
    }

    ngOnInit(): void {
        this.recordStateSubscription = this.panoHeadService.subscribeRecordState(state => this.state = state);
    }

    ngOnDestroy(): void {
        this.recordStateSubscription?.unsubscribe();
    }


    get state(): RecordState {
        return this._state;
    }

    set state(value: RecordState) {
        console.log('STATE', JSON.stringify(value, null, 2));
        this._state = value;
    }

    onStart(): void {
        this.panoHeadService.sendStartRecord();
    }

    onStop(): void {
        this.panoHeadService.sendStopRecord();
    }

    onPause(): void {
        this.panoHeadService.sendPauseResumeRecord();
    }

    private onActivate(): void {
        this.uiService.title.next('Record');
        this.uiService.backButton.next(true);

        this.panoHeadService.requestRecordState(state => this.state = state);
        this.panoService.requestRecalculatePano();
    }
}

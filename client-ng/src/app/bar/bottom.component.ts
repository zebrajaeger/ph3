import {Component, OnInit} from '@angular/core';
import {PanoHeadService} from '../panohead.service';
import {OnDestroy} from '@angular/core/core';
import {Subscription} from 'rxjs';
import {AutomateState, RecordState} from '../../data/record';
import {PanoService} from '../pano.service';
import {CalculatedPano} from '../../data/pano';
import {Power} from '../../data/panohead';

@Component({
    selector: 'app-bottom',
    templateUrl: './bottom.component.html',
    styleUrls: ['./bottom.component.scss']
})
export class BottomComponent implements OnInit, OnDestroy {
    private actorSubscription: Subscription;
    public actorPos: string;

    private recordStateSubscription: Subscription;
    public state: RecordState;

    private calculatedPanoSubscription: Subscription;
    public calc: CalculatedPano;

    private powerSubscription: Subscription;
    public gauge: Power;
    public gaugeString: string;

    constructor(private panoHeadService: PanoHeadService,
                private panoService: PanoService) {
    }

    ngOnInit(): void {
        this.actorSubscription = this.panoHeadService.subscribeActorPosition(actorPositionData => {
            this.actorPos = `${actorPositionData.x.toFixed(3)}°, ${actorPositionData.y.toFixed(3)}°`;
        });
        this.recordStateSubscription = this.panoHeadService.subscribeRecordState(state => {
            this.state = state;
            console.log('foo', this.state);
        });
        this.panoService.requestCalculatedPano(calculatedPano => this.calc = calculatedPano);
        this.powerSubscription = this.panoHeadService.subscribePowerGauge(power => {
            this.gauge = power;
            this.gaugeString = power.toString();
        });
    }

    ngOnDestroy(): void {
        this.actorSubscription?.unsubscribe();
        this.recordStateSubscription?.unsubscribe();
        this.calculatedPanoSubscription?.unsubscribe();
        this.powerSubscription?.unsubscribe();
    }

    isStopped(): boolean {
        const r = !this.state ||
            (this.state?.automateState === AutomateState.STOPPED ||
                this.state?.automateState === AutomateState.STOPPED_WITH_ERROR);
        return r;
    }
}

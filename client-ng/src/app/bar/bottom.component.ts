import {Component, OnInit} from '@angular/core';
import {PanoHeadService} from '../panohead.service';
import {OnDestroy} from '@angular/core/core';
import {Subscription} from 'rxjs';
import {RecordState} from '../../data/record';

@Component({
    selector: 'app-bottom',
    templateUrl: './bottom.component.html',
    styleUrls: ['./bottom.component.scss']
})
export class BottomComponent implements OnInit, OnDestroy {
    public actorPos: string;
    private actorSubscription: Subscription;

    private recordStateSubscription: Subscription;
    public state: RecordState;

    constructor(private panoHeadService: PanoHeadService) {
    }

    ngOnInit(): void {
        this.actorSubscription = this.panoHeadService.subscribeActor(actorData => {
            this.actorPos = `${actorData.x.pos.toFixed(3)}, ${actorData.y.pos.toFixed(3)}`;
        });
        this.recordStateSubscription = this.panoHeadService.subscribeRecordState(state => this.state = state);
    }

    ngOnDestroy(): void {
        this.actorSubscription?.unsubscribe();
        this.recordStateSubscription?.unsubscribe();
    }
}

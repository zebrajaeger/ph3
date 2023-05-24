import {Component, OnDestroy, OnInit} from '@angular/core';
import {RouterService} from '../service/router.service';
import {UiService} from '../service/ui.service';
import {PanoHeadService} from '../service/panohead.service';
import {AutomateState, RobotState} from '../../data/record';
import {Subscription} from 'rxjs';
import {PanoService} from '../service/pano.service';
import {Pattern} from "../../data/pano";

@Component({
    selector: 'app-record',
    templateUrl: './record.component.html',
    styleUrls: ['./record.component.scss']
})
export class RecordComponent implements OnInit, OnDestroy {
    private robotStateSubscription!: Subscription;
    public _robotState!: RobotState;

    private patternSubscription!: Subscription;
    gridChecked: any;
    sparseChecked: any;

    public done: number = 0;
    public color?: string;

    public msg: string = '';

    constructor(private routerService: RouterService,
                private panoHeadService: PanoHeadService,
                private panoService: PanoService,
                private uiService: UiService) {
        routerService.onActivate(this, () => this.onActivate());
    }

    ngOnInit(): void {
        this.robotStateSubscription = this.panoHeadService.subscribeRobotState(robotState => this.robotState = robotState);
        this.panoHeadService.requestRobotState(robotState => this.robotState = robotState)

        this.patternSubscription = this.panoService.subscribePatternType(pattern => this.pattern = pattern);
        this.panoService.requestPatternType(pattern => this.pattern = pattern)
    }

    ngOnDestroy(): void {
        this.robotStateSubscription?.unsubscribe();
        this.patternSubscription?.unsubscribe();
    }

    set robotState(value: RobotState) {
        this._robotState = value;
        const cmd = value?.command;
        if (value.automateState !== AutomateState.STOPPED) {
            this.msg = `[${value?.commandIndex + 1}/${value?.commandCount}] ${cmd?.description}`;
        } else {
            this.msg = '';
        }
    }

    set pattern(pattern: Pattern) {
        switch (pattern) {
            case Pattern.GRID: {
                this.gridChecked = true;
                this.sparseChecked = false;
                break;
            }
            case Pattern.SPARSE: {
                this.gridChecked = false;
                this.sparseChecked = true;
                break;
            }
        }
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

        this.panoHeadService.requestRobotState(state => this.robotState = state);
        this.panoService.requestRecalculatePano();
    }

    onPatternClick(value: string) {
        switch (value) {
            case "GRID":
                this.panoService.setPatternType(Pattern.GRID);
                break;
            case "SPARSE":
                this.panoService.setPatternType(Pattern.SPARSE);
                break;
        }
    }
}

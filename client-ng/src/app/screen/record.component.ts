import {Component, OnDestroy, OnInit} from '@angular/core';
import {RouterService} from '../service/router.service';
import {UiService} from '../service/ui.service';
import {PanoHeadService} from '../service/panohead.service';
import {AutomateState, RobotState} from '../../data/record';
import {Subscription} from 'rxjs';
import {PanoService} from '../service/pano.service';

@Component({
  selector: 'app-record',
  templateUrl: './record.component.html',
  styleUrls: ['./record.component.scss']
})
export class RecordComponent implements OnInit, OnDestroy {
  private robotStateSubscription!: Subscription;
  public _robotState!: RobotState;

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
  }

  ngOnDestroy(): void {
    this.robotStateSubscription?.unsubscribe();
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
}

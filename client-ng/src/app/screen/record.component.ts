import {Component, OnDestroy, OnInit} from '@angular/core';
import {RouterService} from '../router.service';
import {UiService} from '../ui.service';
import {PanoHeadService} from '../panohead.service';
import {AutomateState, RecordState} from '../../data/record';
import {Subscription} from 'rxjs';
import {PanoService} from '../pano.service';
import {CalculatedPano} from "../../data/pano";

@Component({
  selector: 'app-record',
  templateUrl: './record.component.html',
  styleUrls: ['./record.component.scss']
})
export class RecordComponent implements OnInit, OnDestroy {
  private recordStateSubscription!: Subscription;
  public _state!: RecordState;

  private calculatedPanoSubscription!: Subscription;
  public calc?: CalculatedPano;

  public done: number = 0;
  public x?: number;
  public y?: number;
  public color?: string;

  public msg: string = '';

  constructor(private routerService: RouterService,
              private panoHeadService: PanoHeadService,
              private panoService: PanoService,
              private uiService: UiService) {
    routerService.onActivate(this, () => this.onActivate());
  }

  ngOnInit(): void {
    this.recordStateSubscription = this.panoHeadService.subscribeRecordState(state => this.state = state);
    this.calculatedPanoSubscription = this.panoService.subscribeCalculatedPano(calculatedPano => {
      console.log('RECALCULATED!!!!', calculatedPano)
      this.calc = calculatedPano
      this.x = calculatedPano.horizontalPositions.length;
      this.y = calculatedPano.verticalPositions.length;
    });
  }

  ngOnDestroy(): void {
    this.recordStateSubscription?.unsubscribe();
    this.calculatedPanoSubscription?.unsubscribe();
  }

  get state(): RecordState {
    return this._state;
  }

  set state(value: RecordState) {
    this._state = value;
    const cmd = value?.command;
    if (value.automateState !== AutomateState.STOPPED) {
      const shotPos = cmd?.shotPosition;
      this.x = shotPos?.xLength;
      this.y = shotPos?.yLength;
      if (shotPos && shotPos.index >= 0) {
        this.done = shotPos.index + 1;
      }
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

    this.panoHeadService.requestRecordState(state => this.state = state);
    this.panoService.requestRecalculatePano();
  }
}

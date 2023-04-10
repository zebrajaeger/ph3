import {Component, OnDestroy, OnInit} from '@angular/core';
import {PanoHeadService} from '../panohead.service';
import {Subscription} from 'rxjs';
import {AutomateState, RecordState} from '../../data/record';
import {PanoService} from '../pano.service';
import {CalculatedPano} from '../../data/pano';
import {Power} from '../../data/panohead';
import {ModalService} from '../ui/modal.service';
import {SystemService} from '../system.service';

@Component({
  selector: 'app-bottom',
  templateUrl: './bottom.component.html',
  styleUrls: ['./bottom.component.scss']
})
export class BottomComponent implements OnInit, OnDestroy {
  private actorSubscription!: Subscription;
  public actorPos!: string;

  private recordStateSubscription!: Subscription;
  public state?: RecordState;
  public stateMsg?: string;
  private calculatedPanoSubscription!: Subscription;
  public calc?: CalculatedPano;
  public panoMsg? : string;

  private powerSubscription!: Subscription;
  public gauge!: Power;
  public gaugeString!: string;

  constructor(private panoHeadService: PanoHeadService,
              private panoService: PanoService,
              public modalService: ModalService,
              private systemService: SystemService) {
  }

  ngOnInit(): void {
    this.actorSubscription = this.panoHeadService.subscribeActorPosition(actorPositionData => {
      this.actorPos = `${actorPositionData.x.toFixed(3)}°, ${actorPositionData.y.toFixed(3)}°`;
    });
    this.recordStateSubscription = this.panoHeadService.subscribeRecordState(state => {
      this.state = state;
      this.stateMsg = `[${state?.commandIndex + 1}/${state?.commandCount}] [${state?.pauseState}]: ${state?.command?.description}`;
    });
    this.panoService.subscribeCalculatedPano(calculatedPano => {
      this.calc = calculatedPano;
      this.panoMsg = `${this.calc?.horizontalPositions.length}, ${this.calc?.verticalPositions.length}`
    });
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
    return !this.state ||
        (this.state?.automateState === AutomateState.STOPPED ||
            this.state?.automateState === AutomateState.STOPPED_WITH_ERROR);
  }

  shutdown(): void {
    this.systemService.shutdown();
    console.log('shutdown');
  }
}

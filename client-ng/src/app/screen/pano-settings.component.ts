import {Component, OnDestroy, OnInit} from '@angular/core';
import {UiService} from '../service/ui.service';
import {Subscription} from 'rxjs';
import {ConnectionService} from '../service/connection.service';
import {RouterService} from '../service/router.service';
import {ModalService} from '../ui/modal.service';
import {PanoService} from '../service/pano.service';
import {Shots} from '../../data/camera';
import {Delay} from '../../data/pano';

@Component({
  selector: 'app-pano-settings',
  templateUrl: './pano-settings.component.html',
  styleUrls: ['./pano-settings.component.scss']
})
export class PanoSettingsComponent implements OnInit, OnDestroy {
  private openSubscription!: Subscription;

  private shotSubscription!: Subscription;
  private _shots: Shots | undefined;

  private delaySubscription!: Subscription;
  private _delay?: Delay;

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
    // this.shotSubscription = this.panoService.subscribeShots((shots) => this.shots = shots);
    this.delaySubscription = this.panoService.subscribeDelay((delay) => this.delay = delay);
  }

  ngOnDestroy(): void {
    this.openSubscription?.unsubscribe();
    this.shotSubscription?.unsubscribe();
    this.delaySubscription?.unsubscribe();
  }

  get shots(): Shots | undefined {
    return this._shots;
  }

  set shots(shots: Shots | undefined) {
    this._shots = shots;
    if (!shots) {
      return;
    }

    // const defaultShots = shots.get('default');
    // if (defaultShots) {
    //   const defaultShot = defaultShots[0];
    //   if (defaultShot) {
    //     this.focusTime = defaultShot.focusTimeMs / 1000;
    //     this.triggerTime = defaultShot.triggerTimeMs / 1000;
    //   }
    // }
  }


  get delay(): Delay | undefined {
    return this._delay;
  }

  set delay(value: Delay | undefined) {
    this._delay = value;
    if (!value) {
      return;
    }
    this.delayAfterMove = value.waitAfterMove / 1000;
  }

  private onActivate(): void {
    this.uiService.title.next('Pano Settings');
    this.uiService.backButton.next(true);
    // this.panoService.requestShots(shots => this.shots = shots);
    // this.panoService.requestDelay(delay => this.delay = delay);
    // this.panoService.requestRecalculatePano();
  }

  onFocusTime(value: number): void {
    this.focusTime = value;
  }

  onFocusTimeClose(): void {
    // this.panoService.setShotFocusTimeMs('default', 0, this.focusTime * 1000);
  }

  onTriggerTimeClose(): void {
    // this.panoService.setShotTriggerTimeMs('default', 0, this.triggerTime * 1000);
  }

  onDelayAfterMoveClose(): void {
    this.panoService.setDelayWaitAfterMoveMs(this.delayAfterMove * 1000);
  }
}

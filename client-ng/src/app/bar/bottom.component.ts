import {Component, OnDestroy, OnInit} from '@angular/core';
import {PanoHeadService} from '../service/panohead.service';
import {Subscription} from 'rxjs';
import {PanoService} from '../service/pano.service';
import {CalculatedPano, FieldOfView, FieldOfViewPartial} from '../../data/pano';
import {Position, Power} from '../../data/panohead';
import {ModalService} from '../ui/modal.service';
import {SystemService} from '../service/system.service';
import {RobotState} from "../../data/record";

@Component({
  selector: 'app-bottom',
  templateUrl: './bottom.component.html',
  styleUrls: ['./bottom.component.scss']
})
export class BottomComponent implements OnInit, OnDestroy {
  private actorSubscription!: Subscription;
  public actorPos?: Position;

  private calculatedPanoSubscription!: Subscription;
  public calc?: CalculatedPano;
  public panoMsg?: string;

  private powerSubscription!: Subscription;
  public gauge!: Power;
  public gaugeString!: string;

  private robotStateSubscription!: Subscription;
  public robotState?: RobotState;

  private pictureFovSubscription!: Subscription;
  public pictureFov?: FieldOfView;
  private panoFovSubscription!: Subscription;
  public panoFov?: FieldOfViewPartial;

  constructor(private panoHeadService: PanoHeadService,
              private panoService: PanoService,
              public modalService: ModalService,
              private systemService: SystemService) {
  }

  ngOnInit(): void {
    this.actorSubscription = this.panoHeadService.subscribeActorPosition(position => {
      this.actorPos = position;
    });

    this.panoService.subscribeCalculatedPano(calculatedPano => {
      this.calc = calculatedPano;
      this.panoMsg = `${this.calc?.horizontalPositions.length}, ${this.calc?.verticalPositions.length}`
    });

    this.powerSubscription = this.panoHeadService.subscribePowerGauge(power => {
      this.gauge = power;
      this.gaugeString = power.toString();
    });

    this.robotStateSubscription = this.panoHeadService.subscribeRobotState(robotState => this.robotState = robotState);
    this.panoHeadService.requestRobotState(robotState => this.robotState = robotState);


    this.pictureFovSubscription = this.panoService.subscribePictureFov(fov => this.pictureFov = fov);
    this.panoService.requestPictureFov(fov => this.pictureFov = fov);

    this.panoFovSubscription = this.panoService.subscribePanoFov(fov => this.panoFov = fov);
    this.panoService.requestPanoFov(fov => this.panoFov = fov);
  }

  ngOnDestroy(): void {
    this.actorSubscription?.unsubscribe();
    this.calculatedPanoSubscription?.unsubscribe();
    this.powerSubscription?.unsubscribe();
    this.robotStateSubscription?.unsubscribe();
    this.pictureFovSubscription?.unsubscribe();
    this.panoFovSubscription?.unsubscribe();
  }

  shutdown(): void {
    this.systemService.shutdown();
    console.log('shutdown');
  }

  reboot(): void {
    this.systemService.reboot();
    console.log('reboot');
  }

  restartApp(): void {
    this.systemService.restartApp();
    console.log('restartApp');
  }
}

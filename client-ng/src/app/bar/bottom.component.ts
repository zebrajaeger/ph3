import {Component, OnDestroy, OnInit} from '@angular/core';
import {PanoHeadService} from '../service/panohead.service';
import {Subscription} from 'rxjs';
import {PanoService} from '../service/pano.service';
import {CameraOfView, Gps, PanoFieldOfView, PanoMatrix} from '../../data/pano';
import {Position, Power} from '../../data/panohead';
import {SystemService} from '../service/system.service';
import {RobotState} from "../../data/record";
import {GpsService} from "../service/gps.service";

@Component({
    selector: 'app-bottom',
    templateUrl: './bottom.component.html',
    styleUrls: ['./bottom.component.scss']
})
export class BottomComponent implements OnInit, OnDestroy {
    private actorSubscription!: Subscription;
    public actorPos?: Position;

    private calculatedPanoSubscription!: Subscription;
    public panoMatrix?: PanoMatrix;
    public panoMsg?: string;

    private powerSubscription!: Subscription;
    public gauge!: Power;
    public gaugeString!: string;

    private robotStateSubscription!: Subscription;
    public robotState?: RobotState;

    private pictureFovSubscription!: Subscription;
    public pictureFov?: CameraOfView;
    private panoFovSubscription!: Subscription;
    public panoFov?: PanoFieldOfView;

    private gpsSubscription!: Subscription;
    public gps?: Gps;


    showSystemDialog = false;

    constructor(private panoHeadService: PanoHeadService,
                private panoService: PanoService,
                private gpsService: GpsService,
                private systemService: SystemService) {
    }

    ngOnInit(): void {
        this.actorSubscription = this.panoHeadService.subscribeActorPosition(position => {
            this.actorPos = position;
        });

        this.panoService.subscribePanoMatrix(panoMatrix => {
            this.panoMatrix = panoMatrix;
            this.panoMsg = `${this.panoMatrix?.maxXSize}, ${this.panoMatrix?.ySize} → ${this.panoMatrix?.positionCount}`
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

        this.gpsSubscription = this.gpsService.subscribeGps(gps => this.gps = gps);
        this.gpsService.requestGps(gps => this.gps = gps);
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

    onOpenSystemDialog() {
        this.showSystemDialog = true;
    }

    onCancelShotdownDialog() {
        this.showSystemDialog = false;
    }
}

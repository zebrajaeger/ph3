import {Injectable} from '@angular/core';
import {Subscription, firstValueFrom} from 'rxjs';
import {map} from 'rxjs/operators';
import {Border, CalculatedPano, Delay, FieldOfView, FieldOfViewPartial} from '../../data/pano';
import {Shots} from '../../data/camera';
import {RxStompService} from "./rx-stomp.service";
import {RxStompRPCService} from "./rx-stomp-rpc.service";

@Injectable({
    providedIn: 'root'
})
export class PanoService {

    constructor(private rxStompService: RxStompService, private rxStompRPCService: RxStompRPCService) {
    }

    // <editor-fold desc="Delay">
    subscribeDelay(cb: (delay: Delay) => void): Subscription {
        return this.rxStompService
            .watch('/topic/delay')
            .pipe(map(msg => JSON.parse(msg.body) as Delay))
            .subscribe(cb);
    }

    requestDelay(cb: (delay: Delay) => void): void {
        this.rxStompRPCService
            .rpc({destination: '/rpc/delay'})
            .pipe(map(msg => JSON.parse(msg.body) as Delay))
            .subscribe(cb);
    }

    setDelayWaitAfterMoveMs(waitAfterMoveMs: number): void {
        const body = waitAfterMoveMs.toString();
        this.rxStompService.publish({destination: '/delay/waitAfterMoveMs', body});
    }

    setDelayWaitBetweenShotsMs(waitBetweenShotsMs: number): void {
        const body = waitBetweenShotsMs.toString();
        this.rxStompService.publish({destination: '/delay/waitBetweenShotsMs', body});
    }

    setDelayWaitAfterShotMs(waitAfterShotMs: number): void {
        const body = waitAfterShotMs.toString();
        this.rxStompService.publish({destination: '/delay/waitAfterShotMs', body});
    }

    // </editor-fold>

    // <editor-fold desc="Picture FOV">
    subscribePictureFov(cb: (fov: FieldOfView) => void): Subscription {
        return this.rxStompService
            .watch('/topic/picture/fov')
            .pipe(map(msg => JSON.parse(msg.body) as FieldOfView))
            .subscribe(cb);
    }

    requestPictureFov(cb: (fov: FieldOfView) => void): void {
        this.rxStompRPCService
            .rpc({destination: '/rpc/picture/fov'})
            .pipe(map(msg => JSON.parse(msg.body) as FieldOfView))
            .subscribe(cb);
    }

    setPictureBorder(border: Border): void {
        const body = JSON.stringify([border]);
        this.rxStompService.publish({destination: '/picture/border', body});
    }

    // </editor-fold>

    // <editor-fold desc="Pano FOV">
    subscribePanoFov(cb: (fov: FieldOfViewPartial) => void): Subscription {
        return this.rxStompService
            .watch('/topic/pano/fov')
            .pipe(map(msg => JSON.parse(msg.body) as FieldOfViewPartial))
            .subscribe(cb);
    }

    requestPanoFov(cb: (fov: FieldOfViewPartial) => void): void {
        this.rxStompRPCService
            .rpc({destination: '/rpc/pano/fov'})
            .pipe(map(msg => JSON.parse(msg.body) as FieldOfViewPartial))
            .subscribe(cb);
    }

    setPanoBorder(border: Border): void {
        const body = JSON.stringify([border]);
        this.rxStompService.publish({destination: '/pano/border', body});
    }

    setPanoPartial(partial: boolean): void {
        this.rxStompService.publish({destination: '/pano/partial', body: partial.toString()});
    }

    // </editor-fold>

    // <editor-fold desc="Shot">
    subscribeShots(cb: (shots: Shots) => void): Subscription {
        return this.rxStompService
            .watch('/topic/shot')
            .pipe(map(msg => new Map(Object.entries(JSON.parse(msg.body))) as Shots))
            .subscribe(cb);
    }

    requestShots(cb: (shots: Shots) => void): Subscription {
        return this.rxStompRPCService
            .rpc({destination: '/rpc/shot'})
            .pipe(map(msg => new Map(Object.entries(JSON.parse(msg.body))) as Shots))
            .subscribe(cb);
    }

    setShotFocusTimeMs(shotsName: string, shotIndex: number, focusTimeMs: number): void {
        const destination = `/shot/${shotsName}/${shotIndex}/focusTimeMs`;
        this.rxStompService.publish({destination, body: focusTimeMs.toString()});
    }

    setShotTriggerTimeMs(shotsName: string, shotIndex: number, triggerTimeMs: number): void {
        const destination = `/shot/${shotsName}/${shotIndex}/triggerTimeMs`;
        this.rxStompService.publish({destination, body: triggerTimeMs.toString()});
    }

    setShot(shotId: string, focusTimeMs: number, triggerTimeMs: number): void {
        const body = JSON.stringify({focusTimeMs, triggerTimeMs});
        this.rxStompService.publish({destination: `/shot/${shotId}/triggerTimeMs`, body});
    }

    // </editor-fold>

    // <editor-fold desc="Calculated Pano">
    subscribeCalculatedPano(cb: (calculatedPano: CalculatedPano) => void): Subscription {
        return this.rxStompService
            .watch('/topic/pano/calculated')
            .pipe(map(msg => JSON.parse(msg.body) as CalculatedPano))
            .subscribe(cb);
    }

    requestCalculatedPano(cb: (calculatedPano: CalculatedPano) => void): Subscription {
        return this.rxStompRPCService
            .rpc({destination: '/rpc/pano/calculated'})
            .pipe(map(msg => JSON.parse(msg.body) as CalculatedPano))
            .subscribe(cb);
    }

    requestRecalculatePano(): void {
        console.log("REQUEST RECALCULATION");
        firstValueFrom(this.rxStompRPCService.rpc({destination: '/rpc/pano/recalculate'})).then();
    }

    // </editor-fold>
}

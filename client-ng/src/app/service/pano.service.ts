import {Injectable} from '@angular/core';
import {firstValueFrom, Subscription} from 'rxjs';
import {map} from 'rxjs/operators';
import {Border, CameraOfView, Delay, PanoFieldOfView, PanoMatrix, Pattern} from '../../data/pano';
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
    subscribePictureFov(cb: (fov: CameraOfView) => void): Subscription {
        return this.rxStompService
            .watch('/topic/picture/fov')
            .pipe(map(msg => new CameraOfView(JSON.parse(msg.body) as CameraOfView)))
            .subscribe(cb);
    }

    requestPictureFov(cb: (fov: CameraOfView) => void): void {
        this.rxStompRPCService
            .rpc({destination: '/rpc/picture/fov'})
            .pipe(map(msg => new CameraOfView(JSON.parse(msg.body) as CameraOfView)))
            .subscribe(cb);
    }

    setPictureBorder(border: Border): void {
        const body = JSON.stringify([border]);
        this.rxStompService.publish({destination: '/picture/border', body});
    }

    setCurrentPictureFovAs(name: string) {
        const body = name;
        this.rxStompService.publish({destination: '/picture/fov/save', body});
    }

    loadPictureFov(name: string) {
        const body = name;
        this.rxStompService.publish({destination: '/picture/fov/load', body});
    }

    deletePictureFov(name: string) {
        const body = name;
        this.rxStompService.publish({destination: '/picture/fov/delete', body});
    }

    subscribePictureFovNames(cb: (names: string[]) => void) {
        return this.rxStompService
            .watch('/topic/picture/fov/names')
            .pipe(map(msg => JSON.parse(msg.body) as string[]))
            .subscribe(cb);
    }

    requestPictureFovNames(cb: (names: string[]) => void) {
        this.rxStompRPCService
            .rpc({destination: '/rpc/picture/fov/names'})
            .pipe(map(msg => JSON.parse(msg.body) as string[]))
            .subscribe(cb);
    }

    // </editor-fold>

    // <editor-fold desc="Pano FOV">
    subscribePanoFov(cb: (fov: PanoFieldOfView) => void): Subscription {
        return this.rxStompService
            .watch('/topic/pano/fov')
            .pipe(map(msg => new PanoFieldOfView(JSON.parse(msg.body) as PanoFieldOfView)))
            .subscribe(cb);
    }

    requestPanoFov(cb: (fov: PanoFieldOfView) => void): void {
        this.rxStompRPCService
            .rpc({destination: '/rpc/pano/fov'})
            .pipe(map(msg => new PanoFieldOfView(JSON.parse(msg.body) as PanoFieldOfView)))
            .subscribe(cb);
    }

    setPanoBorder(border: Border): void {
        const body = JSON.stringify([border]);
        this.rxStompService.publish({destination: '/pano/border', body});
    }

    setPanoFullX(partial: boolean): void {
        this.rxStompService.publish({destination: '/pano/fullX', body: partial.toString()});
    }

    setPanoFullY(partial: boolean): void {
        this.rxStompService.publish({destination: '/pano/fullY', body: partial.toString()});
    }

    // </editor-fold>

    // <editor-fold desc="Calculated Pano">
    subscribePanoMatrix(cb: (panoMatrix: PanoMatrix) => void): Subscription {
        return this.rxStompService
            .watch('/topic/pano/matrix')
            .pipe(map(msg => JSON.parse(msg.body) as PanoMatrix))
            .subscribe(cb);
    }

    requestPanoMatrix(cb: (panoMatrix: PanoMatrix) => void): Subscription {
        return this.rxStompRPCService
            .rpc({destination: '/rpc/pano/matrix'})
            .pipe(map(msg => JSON.parse(msg.body) as PanoMatrix))
            .subscribe(cb);
    }

    requestRecalculatePano(): void {
        console.log("REQUEST RECALCULATION");
        firstValueFrom(this.rxStompRPCService.rpc({destination: '/rpc/pano/recalculate'})).then();
    }

    // </editor-fold>

    subscribePatternType(cb: (pattern: Pattern) => void): Subscription {
        return this.rxStompService
            .watch('/topic/pano/pattern')
            .pipe(map(msg => JSON.parse(msg.body) as Pattern))
            .subscribe(cb);
    }

    requestPatternType(cb: (panoMatrix: Pattern) => void): Subscription {
        return this.rxStompRPCService
            .rpc({destination: '/rpc/pano/pattern'})
            .pipe(map(msg => JSON.parse(msg.body) as Pattern))
            .subscribe(cb);
    }

    setPatternType(pattern: Pattern): void {
        const body = JSON.stringify(pattern);
        this.rxStompService.publish({destination: '/pano/pattern', body});
    }

}

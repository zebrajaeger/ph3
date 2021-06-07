import {Injectable} from '@angular/core';
import {RxStompRPCService, RxStompService} from '@stomp/ng2-stompjs';
import {Observable, Subscription} from 'rxjs';
import {map} from 'rxjs/operators';
import {Border, CalculatedPano, FieldOfView, FieldOfViewPartial} from '../data/pano';

@Injectable({
    providedIn: 'root'
})
export class PanoService {

    constructor(private rxStompService: RxStompService, private rxStompRPCService: RxStompRPCService) {
    }

    // <editor-fold desc="Picture">
    pictureFov(): Observable<FieldOfView> {
        return this.rxStompService
            .watch('/topic/picture/fov')
            .pipe(map(msg => JSON.parse(msg.body) as FieldOfView));
    }

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

    // <editor-fold desc="Calculated Pano">
    calculatedPano(): Observable<CalculatedPano> {
        return this.rxStompService
            .watch('/topic/pano/calculated')
            .pipe(map(msg => JSON.parse(msg.body) as CalculatedPano));
    }

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

    // </editor-fold>

    // <editor-fold desc="Pano FOV">
    panoFov(): Observable<FieldOfViewPartial> {
        return this.rxStompService
            .watch('/topic/pano/fov')
            .pipe(map(msg => JSON.parse(msg.body) as FieldOfViewPartial));
    }

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
}

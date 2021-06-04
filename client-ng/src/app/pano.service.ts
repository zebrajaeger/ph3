import {Injectable} from '@angular/core';
import {RxStompRPCService, RxStompService} from '@stomp/ng2-stompjs';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {Border, CalculatedPano, FieldOfView, FieldOfViewPartial} from '../data/pano';

@Injectable({
    providedIn: 'root'
})
export class PanoService {

    constructor(private rxStompService: RxStompService) {
    }

    pictureFov(): Observable<FieldOfView> {
        return this.rxStompService
            .watch('/topic/picture/fov')
            .pipe(map(msg => JSON.parse(msg.body) as FieldOfView));
    }

    calculatedPano(): Observable<CalculatedPano> {
        return this.rxStompService
            .watch('/topic/pano/calculated')
            .pipe(map(msg => JSON.parse(msg.body) as CalculatedPano));
    }

    setPictureBorder(border: Border): void {
        const body = JSON.stringify([border]);
        this.rxStompService.publish({destination: '/picture/border', body});
    }

    panoFov(): Observable<FieldOfViewPartial> {
        return this.rxStompService
            .watch('/topic/pano/fov')
            .pipe(map(msg => JSON.parse(msg.body) as FieldOfViewPartial));
    }

    setPanoBorder(border: Border): void {
        const body = JSON.stringify([border]);
        this.rxStompService.publish({destination: '/pano/border', body});
    }

    setPanoPartial(partial: boolean): void {
        this.rxStompService.publish({destination: '/pano/partial', body: partial.toString()});
    }
}
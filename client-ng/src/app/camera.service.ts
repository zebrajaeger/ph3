import {Injectable} from '@angular/core';
import {map} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {Camera, Shot} from '../data/camera';
import {RxStompService} from "./rx-stomp.service";

@Injectable({
    providedIn: 'root'
})
export class CameraService {

    constructor(private rxStompService: RxStompService) {
    }

    public camera(): Observable<Camera> {
        return this.rxStompService
            .watch('/topic/camera/')
            .pipe(map(msg => JSON.parse(msg.body) as Camera));
    }

    focus(focusTimeMs: number): void {
        this.rxStompService.publish({destination: '/camera/focus', body: focusTimeMs.toString()});
    }

    trigger(triggerTimeMs: number): void {
        this.rxStompService.publish({destination: '/camera/trigger', body: triggerTimeMs.toString()});
    }

    shot(focusTimeMs: number, triggerTimeMs: number): void {
        const data: Shot = {focusTimeMs, triggerTimeMs};
        this.rxStompService.publish({destination: '/camera/shot', body: JSON.stringify(data)});
    }
}

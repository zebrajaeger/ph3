import {Injectable} from '@angular/core';
import {RxStompRPCService, RxStompService} from '@stomp/ng2-stompjs';
import {map} from 'rxjs/operators';
import {Observable, Subscription} from 'rxjs';
import {Actor} from '../data/panohead';

@Injectable({
    providedIn: 'root'
})
export class PanoHeadService {

    constructor(private rxStompService: RxStompService, private rxStompRPCService: RxStompRPCService) {
    }

    // <editor-fold desc="Actor status">
    public actor(): Observable<Actor> {
        return this.rxStompService
            .watch('/topic/actor/')
            .pipe(map(msg => JSON.parse(msg.body) as Actor));
    }

    public subscribeActor(cb: (actor: Actor) => void): Subscription {
        return this.rxStompService
            .watch('/topic/actor/')
            .pipe(map(msg => JSON.parse(msg.body) as Actor))
            .subscribe(cb);
    }

    public requestActor(cb: (actor: Actor) => void): Subscription {
        return this.rxStompRPCService
            .rpc({destination: '/topic/actor/'})
            .pipe(map(msg => JSON.parse(msg.body) as Actor))
            .subscribe(cb);
    }

    // </editor-fold>

    sendJogging(isJogging: boolean): void {
        this.rxStompService.publish({destination: '/actor/jogging', body: isJogging.toString()});
    }

    // <editor-fold desc="Record">
    sendStartRecord(): void {
        this.rxStompService.publish({destination: '/record/start'});
    }

    sendStopRecord(): void {
        this.rxStompService.publish({destination: '/record/stop'});
    }

    sendPauseResumeRecord(): void {
        this.rxStompService.publish({destination: '/record/pause'});
    }

    // </editor-fold>
}

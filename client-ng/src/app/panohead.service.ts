import {Injectable} from '@angular/core';
import {map} from 'rxjs/operators';
import {Subscription} from 'rxjs';
import {Actor, ActorState, Position, Power} from '../data/panohead';
import {RecordState} from '../data/record';
import {RxStompService} from "./rx-stomp.service";
import {RxStompRPCService} from "./rx-stomp-rpc.service";

@Injectable({
    providedIn: 'root'
})
export class PanoHeadService {

    constructor(private rxStompService: RxStompService, private rxStompRPCService: RxStompRPCService) {
    }

    // <editor-fold desc="Actor status">
    public subscribePowerGauge(cb: (actorState: Power) => void): Subscription {
        return this.rxStompService
            .watch('/topic/power/')
            .pipe(map(msg => new Power(msg.body)))
            .subscribe(cb);
    }

    public subscribeActorState(cb: (actorState: ActorState) => void): Subscription {
        return this.rxStompService
            .watch('/topic/actor/state/')
            .pipe(map(msg => JSON.parse(msg.body) as ActorState))
            .subscribe(cb);
    }

    public subscribeActorPosition(cb: (actorState: ActorState) => void): Subscription {
        return this.rxStompService
            .watch('/topic/actor/position/')
            .pipe(map(msg => JSON.parse(msg.body) as Position))
            .subscribe(cb);
    }

    public requestActor(cb: (actor: Actor) => void): Subscription {
        return this.rxStompRPCService
            .rpc({destination: '/topic/actor/'})
            .pipe(map(msg => JSON.parse(msg.body) as Actor))
            .subscribe(cb);
    }

    // </editor-fold>
    sendSetToZero(): void {
        this.rxStompService.publish({destination: '/actor/setToZero'});
    }

    sendJogging(isJogging: boolean): void {
        this.rxStompService.publish({destination: '/actor/jogging', body: isJogging.toString()});
    }

    // <editor-fold desc="Record">
    public subscribeRecordState(cb: (actor: RecordState) => void): Subscription {
        return this.rxStompService
            .watch('/topic/robot/state')
            .pipe(map(msg => new RecordState(msg.body)))
            .subscribe(cb);
    }

    public requestRecordState(cb: (actor: RecordState) => void): Subscription {
        return this.rxStompRPCService
            .rpc({destination: '/rpc/robot/state'})
            .pipe(map(msg => new RecordState(msg.body)))
            .subscribe(cb);
    }

    sendStartRecord(): void {
        this.rxStompService.publish({destination: '/record/start/default'});
    }

    sendStopRecord(): void {
        this.rxStompService.publish({destination: '/record/stop'});
    }

    sendPauseResumeRecord(): void {
        this.rxStompService.publish({destination: '/record/pause'});
    }

    // </editor-fold>
}

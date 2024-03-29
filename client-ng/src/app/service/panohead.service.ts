import {Injectable} from '@angular/core';
import {map} from 'rxjs/operators';
import {Subscription} from 'rxjs';
import {Actor, ActorState, Position, Power} from '../../data/panohead';
import {RobotState} from '../../data/record';
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

    public subscribeActorPosition(cb: (position: Position) => void): Subscription {
        return this.rxStompService
            .watch('/topic/actor/position/')
            .pipe(map(msg => JSON.parse(msg.body) as Position))
            .subscribe(cb);
    }

    public subscribeActorActive(cb: (isActive: boolean) => void): Subscription {
        return this.rxStompService
            .watch('/topic/actor/active/')
            .pipe(map(msg => JSON.parse(msg.body) as boolean))
            .subscribe(cb);
    }

    public subscribeActor(cb: (actor: Actor) => void): Subscription {
        return this.rxStompRPCService
            .rpc({destination: '/topic/actor/'})
            .pipe(map(msg => JSON.parse(msg.body) as Actor))
            .subscribe(cb);
    }

    // </editor-fold>
    sendSetToZero(): void {
        this.rxStompService.publish({destination: '/actor/setToZero'});
    }

    sendGoToZero(): void {
        this.rxStompService.publish({destination: '/actor/goToZero'});
    }

    adaptOffset(): void {
        this.rxStompService.publish({destination: '/actor/adaptOffset'});
    }

    sendJogging(isJogging: boolean): void {
        this.rxStompService.publish({destination: '/actor/jogging', body: isJogging.toString()});
    }

    sendManualMove(relPosition : Position): void {
        this.rxStompService.publish({destination: '/actor/manualMove', body: JSON.stringify(relPosition)});
    }

    sendManualMoveByJoystick(speed : Position): void {
        this.rxStompService.publish({destination: '/actor/manualMoveByJoystick', body: JSON.stringify(speed)});
    }

    sendManualMoveByJoystickStop(): void {
        this.rxStompService.publish({destination: '/actor/manualMoveByJoystickStop'});
    }

    // <editor-fold desc="Record">
    public subscribeRobotState(cb: (actor: RobotState) => void): Subscription {
        return this.rxStompService
            .watch('/topic/robot/state')
            .pipe(map(msg => new RobotState(msg.body)))
            .subscribe(cb);
    }

    public requestRobotState(cb: (actor: RobotState) => void): Subscription {
        return this.rxStompRPCService
            .rpc({destination: '/rpc/robot/state'})
            .pipe(map(msg => new RobotState(msg.body)))
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

import { Injectable } from '@angular/core';
import { map } from 'rxjs/operators';
import { Subscription } from 'rxjs';
import { ActorState, AxesPosition, BatteryState, Position, Power } from '../../data/panohead';
import { RecordState } from '../../data/record';
import { RxStompService } from "./rx-stomp.service";
import { RxStompRPCService } from "./rx-stomp-rpc.service";

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

    public subscribeBatteryState(cb: (batteryState: BatteryState) => void): Subscription {
        return this.rxStompService
            .watch('/topic/battery/')
            .pipe(map(msg => JSON.parse(msg.body) as BatteryState))
            .subscribe(cb);
    }

    public subscribeActorState(cb: (actorState: ActorState) => void): Subscription {
        return this.rxStompService
            .watch('/topic/actor/state/')
            .pipe(map(msg => JSON.parse(msg.body) as ActorState))
            .subscribe(cb);
    }

    public subscribeActorPosition(cb: (position: AxesPosition) => void): Subscription {
        return this.rxStompService
            .watch('/topic/actor/position/')
            .pipe(map(msg => JSON.parse(msg.body) as AxesPosition))
            .subscribe(cb);
    }

    public subscribeActorActive(cb: (isActive: boolean) => void): Subscription {
        return this.rxStompService
            .watch('/topic/actor/active/')
            .pipe(map(msg => JSON.parse(msg.body) as boolean))
            .subscribe(cb);
    }

    public subscribeActor(cb: (actor: ActorState) => void): Subscription {
        return this.rxStompRPCService
            .rpc({ destination: '/topic/actor/' })
            .pipe(map(msg => JSON.parse(msg.body) as ActorState))
            .subscribe(cb);
    }

    // </editor-fold>
    sendSetToZero(): void {
        this.rxStompService.publish({ destination: '/actor/setToZero' });
    }

    sendGoToZero(): void {
        this.rxStompService.publish({ destination: '/actor/goToZero' });
    }

    adaptOffset(): void {
        this.rxStompService.publish({ destination: '/actor/adaptOffset' });
    }

    sendJogging(isJogging: boolean): void {
        this.rxStompService.publish({ destination: '/actor/jogging', body: isJogging.toString() });
    }

    public subscribeJoggingState(cb: (isJogging: boolean) => void): Subscription {
        return this.rxStompService
            .watch('/topic/actor/jogging/')
            .pipe(map(msg => this.toBoolean(msg.body)))
            .subscribe(cb);
    }

    sendManualMove(relPosition: Position): void {
        this.rxStompService.publish({ destination: '/actor/manualMove', body: JSON.stringify(relPosition) });
    }

    sendManualMoveForced(relPosition: Position): void {
        this.rxStompService.publish({ destination: '/actor/manualMove/force', body: JSON.stringify(relPosition) });
    }

    sendManualMoveByJoystick(speed: Position): void {
        this.rxStompService.publish({ destination: '/actor/manualMoveByJoystick', body: JSON.stringify(speed) });
    }

    sendManualMoveByJoystickStop(): void {
        this.rxStompService.publish({ destination: '/actor/manualMoveByJoystickStop' });
    }

    // <editor-fold desc="Record">
    public subscribeRecordState(cb: (actor: RecordState) => void): Subscription {
        return this.rxStompService
            .watch('/topic/record/state')
            .pipe(map(msg => new RecordState(msg.body)))
            .subscribe(cb);
    }

    public requestRecordState(cb: (actor: RecordState) => void): Subscription {
        return this.rxStompRPCService
            .rpc({ destination: '/rpc/record/state' })
            .pipe(map(msg => new RecordState(msg.body)))
            .subscribe(cb);
    }

    sendStartRecord(name: string): void {
        this.rxStompService.publish({ destination: '/record/start', body: name });
    }

    sendStopRecord(): void {
        this.rxStompService.publish({ destination: '/record/stop' });
    }

    sendPauseResumeRecord(): void {
        this.rxStompService.publish({ destination: '/record/pause' });
    }

    // </editor-fold>

    toBoolean(value: string): boolean {
        return value === '1' || (value?.toLowerCase?.() === 'true')
    }
}

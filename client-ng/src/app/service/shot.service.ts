import {Injectable} from '@angular/core';
import {Subscription} from 'rxjs';
import {map} from 'rxjs/operators';
import {Shots, ShotsPresets} from '../../data/camera';
import {RxStompService} from "./rx-stomp.service";
import {RxStompRPCService} from "./rx-stomp-rpc.service";

@Injectable({
    providedIn: 'root'
})
export class ShotService {

    constructor(private rxStompService: RxStompService, private rxStompRPCService: RxStompRPCService) {
    }

    subscribeCurrentShots(cb: (shots: Shots) => void): Subscription {
        return this.rxStompService
            .watch('/topic/shots/current')
            .pipe(map(msg => new Shots().setFromJson(JSON.parse(msg.body))))
            .subscribe(cb);
    }

    requestCurrentShots(cb: (shots: Shots) => void): Subscription {
        return this.rxStompRPCService
            .rpc({destination: '/rpc/shots/current'})
            .pipe(map(msg => new Shots().setFromJson(JSON.parse(msg.body))))
            .subscribe(cb);
    }

    subscribeShotsPresets(cb: (presets: ShotsPresets) => void): Subscription {
        return this.rxStompService
            .watch('/topic/shots/presets')
            .pipe(map(msg => new ShotsPresets().setFromJson(JSON.parse(msg.body))))
            .subscribe(cb);
    }

    requestShotsPresets(cb: (shots: ShotsPresets) => void): Subscription {
        return this.rxStompRPCService
            .rpc({destination: '/rpc/shots/presets'})
            .pipe(map(msg => new ShotsPresets().setFromJson(JSON.parse(msg.body))))
            .subscribe(cb);
    }

    sendSetCurrentShots(shots: Shots): void {
        this.rxStompService.publish({
            destination: '/shots/current/set',
            body: JSON.stringify(shots)
        });
    }

    sendSetPresets(presets: ShotsPresets): void {
        const obj = Object.fromEntries(presets);
        this.rxStompService.publish({
            destination: '/shots/presets/set',
            body: JSON.stringify(obj)
        });
    }
}

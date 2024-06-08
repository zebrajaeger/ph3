import {Injectable} from '@angular/core';
import {RxStompService} from "./rx-stomp.service";
import {Gps} from "../../data/pano";
import {Subscription} from "rxjs";
import {map} from "rxjs/operators";
import {RxStompRPCService} from "./rx-stomp-rpc.service";

@Injectable({
    providedIn: 'root'
})
export class GpsService {

    constructor(private rxStompService: RxStompService, private rxStompRPCService: RxStompRPCService) {
    }

    subscribeGps(cb: (gps: Gps) => void): Subscription {
        return this.rxStompService
            .watch('/topic/gps')
            .pipe(map(msg => JSON.parse(msg.body) as Gps))
            .subscribe(cb);
    }

    requestGps(cb: (gps: Gps) => void): void {
        this.rxStompRPCService
            .rpc({destination: '/rpc/gps'})
            .pipe(map(msg => JSON.parse(msg.body) as Gps))
            .subscribe(cb);
    }
}

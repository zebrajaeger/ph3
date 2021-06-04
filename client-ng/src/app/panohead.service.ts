import {Injectable} from '@angular/core';
import {RxStompRPCService, RxStompService} from '@stomp/ng2-stompjs';
import {map} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {Actor, PanoHeadData} from '../data/panohead';

@Injectable({
    providedIn: 'root'
})
export class PanoHeadService {

    constructor(private rxStompService: RxStompService, private rxStompRPCService: RxStompRPCService) {
    }

    public actor(): Observable<Actor> {
        return this.rxStompService
            .watch('/topic/actor/')
            .pipe(map(msg => JSON.parse(msg.body) as Actor));
    }

    jogging(isJogging: boolean): void {
        this.rxStompService.publish({destination: '/actor/jogging', body: isJogging.toString()});
    }
}

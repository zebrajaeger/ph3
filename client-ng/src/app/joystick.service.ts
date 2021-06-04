import {Injectable} from '@angular/core';
import {RxStompRPCService, RxStompService} from '@stomp/ng2-stompjs';
import {JoystickPosition} from '../data/joystick';
import {map} from 'rxjs/operators';
import {Observable} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class JoystickService {

    constructor(private rxStompService: RxStompService, private rxStompRPCService: RxStompRPCService) {
    }

    public position(): Observable<JoystickPosition> {
        return this.rxStompService
            .watch('/topic/joystick/position')
            .pipe(map(msg => JSON.parse(msg.body) as JoystickPosition));
    }

    center(): void {
        this.rxStompService.publish({destination: '/joystick/center'});
    }

    reset(): void {
        this.rxStompService.publish({destination: '/app/joystick/reset'});
    }
}

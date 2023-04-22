import {Injectable} from '@angular/core';
import {JoystickPosition} from '../../data/joystick';
import {map} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {RxStompService} from "./rx-stomp.service";

@Injectable({
    providedIn: 'root'
})
export class JoystickService {

    constructor(private rxStompService: RxStompService) {
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

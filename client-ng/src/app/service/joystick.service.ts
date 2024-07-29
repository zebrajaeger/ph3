import { Injectable } from '@angular/core';
import { JoystickPosition } from '../../data/joystick';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { RxStompService } from "./rx-stomp.service";
import { Position } from 'src/data/panohead';

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

    sendDevicePositionXY(x: number, y: number): void {
        this.rxStompService.publish({
            destination: '/deviceorientation/position',
            body: JSON.stringify({ x, y })
        });
    }

    sendDevicePosition(position: Position): void {
        this.rxStompService.publish({
            destination: '/deviceorientation/position',
            body: JSON.stringify(position)
        });
    }

    center(): void {
        this.rxStompService.publish({ destination: '/joystick/center' });
    }

    reset(): void {
        this.rxStompService.publish({ destination: '/app/joystick/reset' });
    }
}

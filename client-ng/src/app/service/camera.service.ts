import {Injectable} from '@angular/core';
import {map} from 'rxjs/operators';
import {Subscription} from 'rxjs';
import {Camera, Shot} from '../../data/camera';
import {RxStompService} from "./rx-stomp.service";
import {RxStompRPCService} from "./rx-stomp-rpc.service";

@Injectable({
  providedIn: 'root'
})
export class CameraService {

  constructor(private rxStompService: RxStompService, private rxStompRPCService: RxStompRPCService) {
  }

  focus(focusTimeMs: number): void {
    this.rxStompService.publish({destination: '/camera/focus', body: focusTimeMs.toString()});
  }

  trigger(triggerTimeMs: number): void {
    this.rxStompService.publish({destination: '/camera/trigger', body: triggerTimeMs.toString()});
  }

  shot(focusTimeMs: number, triggerTimeMs: number): void {
    const data: Shot = {focusTimeMs, triggerTimeMs};
    this.rxStompService.publish({destination: '/camera/shot', body: JSON.stringify(data)});
  }

  subscribeCamera(cb: (fov: Camera) => void): Subscription {
    return this.rxStompService
    .watch('/topic/camera')
    .pipe(map(msg => JSON.parse(msg.body) as Camera))
    .subscribe(cb);
  }

  public requestCamera(cb: (actor: Camera) => void): Subscription {
    return this.rxStompRPCService
    .rpc({destination: '/rpc/camera'})
    .pipe(map(msg => JSON.parse(msg.body) as Camera))
    .subscribe(cb);
  }
}

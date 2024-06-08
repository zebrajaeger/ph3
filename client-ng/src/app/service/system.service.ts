import {Injectable} from '@angular/core';
import {RxStompService} from "./rx-stomp.service";
import {Subscription} from "rxjs";
import {map} from "rxjs/operators";
import {Version} from "../../data/system";
import {RxStompRPCService} from "./rx-stomp-rpc.service";

@Injectable({
  providedIn: 'root'
})
export class SystemService {
  constructor(private rxStompService: RxStompService,private rxStompRPCService: RxStompRPCService) {
  }

  shutdown(): void {
    this.rxStompService.publish({destination: '/system/shutdown'});
  }

  reboot(): void {
    this.rxStompService.publish({destination: '/system/reboot'});
  }

  restartApp(): void {
    this.rxStompService.publish({destination: '/system/restartApp'});
  }

  public requestVersion(cb: (version: Version) => void): Subscription {
    return this.rxStompRPCService
    .rpc({destination: '/rpc/system/version'})
    .pipe(map(msg => JSON.parse(msg.body) as Version))
    .subscribe(cb);
  }
}

import {Injectable} from '@angular/core';
import {RxStompService} from "./rx-stomp.service";

@Injectable({
  providedIn: 'root'
})
export class SystemService {
  constructor(private rxStompService: RxStompService) {
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
}

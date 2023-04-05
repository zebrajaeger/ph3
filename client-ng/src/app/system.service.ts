import {Injectable} from '@angular/core';
import {RxStompRPCService, RxStompService} from '@stomp/ng2-stompjs';

@Injectable({
  providedIn: 'root'
})
export class SystemService {
  constructor(private rxStompService: RxStompService, private rxStompRPCService: RxStompRPCService) {
  }

  shutdown(): void {
    this.rxStompService.publish({destination: '/system/shutdown'});
  }
}

import { Injectable } from '@angular/core';
import { RxStomp } from '@stomp/rx-stomp';
import { RxStompConfig } from '@stomp/rx-stomp';

@Injectable({
  providedIn: 'root',
})
export class RxStompService extends RxStomp {
  rxStompConfig: RxStompConfig = {
  // brokerURL: 'ws://192.168.178.78:8080/ph',
  brokerURL: 'ws://127.0.0.1:8080/ph',

  heartbeatIncoming: 0,
  heartbeatOutgoing: 10000,
  reconnectDelay: 1000

  // debug: (msg: string): void => {
  //   console.log(new Date(), msg);
  // }
};

  constructor() {
    super();
    this.configure(this.rxStompConfig);
    this.activate();
  }
}

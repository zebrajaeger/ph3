import { Injectable } from '@angular/core';
import { RxStomp } from '@stomp/rx-stomp';
import { RxStompConfig } from '@stomp/rx-stomp';

@Injectable({
  providedIn: 'root',
})
export class RxStompService extends RxStomp {
  rxStompConfig: RxStompConfig = {
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
    let ssl = window.location.protocol === "https:";
    if (ssl) {
      this.rxStompConfig.brokerURL = `wss://${window.location.hostname}:8443/ph`
    } else {
      this.rxStompConfig.brokerURL = `ws://${window.location.hostname}:8080/ph`
    }
    this.configure(this.rxStompConfig);
    this.activate();
  }
}

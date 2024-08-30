import { Injectable } from '@angular/core';
import { RxStomp } from '@stomp/rx-stomp';
import { RxStompConfig } from '@stomp/rx-stomp';

@Injectable({
  providedIn: 'root',
})
export class RxStompService extends RxStomp {
  rxStompConfig: RxStompConfig = {
    heartbeatIncoming: 0,
    heartbeatOutgoing: 1000,
    reconnectDelay: 1000,
    logRawCommunication: true
  };

  constructor() {
    super();

    let ssl = window.location.protocol === "https:";
    if (ssl) {
      this.rxStompConfig.brokerURL = `wss://${window.location.hostname}:8443/ws`
    } else {
      this.rxStompConfig.brokerURL = `ws://${window.location.hostname}:8080/ws`
    }

    const debug = new URLSearchParams(window.location.search).get("debug");
    if (debug === "1" || debug?.toLowerCase() == "true") {
      this.rxStompConfig.debug = (msg: string): void => { console.log("[RXStomp]", msg); }
      console.log("[WS Config]", this.rxStompConfig)
    }

    this.configure(this.rxStompConfig);
    this.activate();
  }
}

import {Injectable} from '@angular/core';
import {Client} from 'rpc-websockets';
import {BehaviorSubject, Observable, Subject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ServerService {
  _client?: Client;
  _isConnected = new BehaviorSubject<boolean>(false);
  _valueSubscriptions = new Map<string, any>();
  _reconnectIntervall = 2000;

  constructor() {
    this.host = 'localhost:8080';
  }

  set host(host: string) {
    if (this._client) {
      this._client.close();
      this._client = undefined;
    }

    this._client = new Client('ws://' + host, {
      autoconnect: true,
      reconnect: true,
      max_reconnects: 0,
      reconnect_interval: this._reconnectIntervall
    });
    this._client.on('open', () => {
      // this._subscriptions = {};
      this.onConnected();
      this._isConnected.next(true);
    });
    this._client.on('error', () => {
      this._isConnected.next(false);
    });
    this._client.on('close', () => {
      this._isConnected.next(false);
    });

    // notify to register values
  }

  private onConnected(): void {
    this._valueSubscriptions.forEach((subject, name) => {
      // @ts-ignore
      this._client.subscribe(name);
      // @ts-ignore
      this._client.on(name, value => {
        subject.next(value);
      });
    });
  }

  onConnectionChanged(callback: (isConnected: boolean) => void): void {
    this._isConnected.subscribe(callback);
  }

  subscribeToValue(name: string, callback: (value: any) => void): void {

    let subject = this._valueSubscriptions.get(name);
    if (!subject) {
      subject = new BehaviorSubject(false);
      this._valueSubscriptions.set(name, subject);

      if (this.isAvailable()) {
        // @ts-ignore
        this._client.subscribe(name);
        // @ts-ignore
        this._client.on(name, value => {
          subject.next(value);
        });
      }
    }

    subject.subscribe(callback);
  }

  isAvailable(): boolean {
    return this._client !== undefined && this._client !== null && this._isConnected.value;
  }

  call(name: string, value?: any): Promise<any> | undefined {
    if (this.isAvailable()) {
      // @ts-ignore
      return this._client.call(name, value);
    }
    return undefined;
  }


  notify(name: string, value: any): Promise<any> | undefined {
    if (this.isAvailable()) {
      // @ts-ignore
      return this._client.notify(name, value);
    }
    return undefined;
  }
}

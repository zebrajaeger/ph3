import {Injectable} from '@angular/core';
import {Client} from 'rpc-websockets';
import {BehaviorSubject, Observable, Subject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ServerService {
  mClient: Client;
  mIsConnected = new BehaviorSubject<boolean>(false);
  mSubscriptions: { [index: string]: any } = {};

  constructor() {
    this.mClient = new Client('ws://localhost:8080');
    this.mClient.on('open', () => {
      this.mSubscriptions = {};
      this.mIsConnected.next(true);
    });
    this.mClient.on('error', () => {
      this.mIsConnected.next(false);
    });
    this.mClient.on('close', () => {
      this.mIsConnected.next(false);
    });
  }

  get isConnected(): Observable<boolean> {
    return this.mIsConnected;
  }

  value(name: string): Observable<any> {
    let subject = this.mSubscriptions[name];
    if (!subject) {
      subject = new BehaviorSubject(false);
      this.mSubscriptions[name] = subject;
      this.mClient.subscribe(name);
      this.mClient.on(name, value => {
        subject.next(value);
      });
    }

    return subject;
  }

  call(name: string, value: any): Promise<any> {
    return this.mClient.call(name, value);
  }

  notify(name: string, value: any): Promise<any> {
    return this.mClient.notify(name, value);
  }
}

import {Injectable} from '@angular/core';

export abstract class StatusListener {
  abstract onStatus(status: string): void;
}

@Injectable({
  providedIn: 'root'
})
export class StatusService {

  listeners: Array<StatusListener> = [];

  constructor() {
  }

  set status(status: string) {
    this.listeners.forEach(l => l.onStatus(status));
  }

  register(listener: StatusListener): void {
    this.listeners.push(listener);
  }
}

import {Injectable} from '@angular/core';
import {Subject} from 'rxjs';

class ComponentCallback {
  activate: Subject<void> = new Subject();
  deactivate: Subject<void> = new Subject();
}

class ComponentCallbacks extends Map<any, ComponentCallback> {

  _active?: any;

  getCallback(component: any): ComponentCallback {
    let cc = this.get(component);
    if (!cc) {
      cc = new ComponentCallback();
      this.set(component, cc);
    }
    return cc;
  }

  set active(component: any) {
    if (this._active) {
      this.getCallback(this._active).deactivate.next();
    }
    this._active = component;
    this.getCallback(this._active).activate.next();
  }
}

@Injectable({
  providedIn: 'root'
})
export class RouterService {
  componentCallbacks = new ComponentCallbacks();

  constructor() {
  }

  onRouteChange(component: any): void {
    this.componentCallbacks.active = component;
  }

  onActivate(component: any, callback: () => void): void {
    this.componentCallbacks.getCallback(component).activate.subscribe(() => {
      callback();
    });
  }

  onDeactivate(component: any, callback: () => void): void {
    this.componentCallbacks.getCallback(component).deactivate.subscribe(() => {
      callback();
    });
  }
}

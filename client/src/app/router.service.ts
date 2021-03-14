import {Injectable} from '@angular/core';
import {Subject} from 'rxjs';

export abstract class ComponentActivation {
  abstract onActivate(): void;

  abstract onDeactivate(): void;
}

export interface ComponentActivationListener {
  onComponentActivated(component: any): void;

  onComponentDeactivated(component: any): void;
}

export interface NavigationBackEnableListener {
  backEnabled: boolean;
}

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

  isActive(component: any): boolean {
    return this._active === component;
  }
}

@Injectable({
  providedIn: 'root'
})
export class RouterService {
  componentActivationListeners: Array<ComponentActivationListener> = [];
  componentCallbacks = new ComponentCallbacks();

  // navigationBackListeners: Array<NavigationBackEnableListener> = [];

  constructor() {
  }

  onRouteChange(component: any): void {
    this.componentCallbacks.active = component;

    // // notify all listeners
    // this.componentActivationListeners.forEach((l) => l.onComponentDeactivated(this.currentActivatedComponent));
    //
    // // notify fresh activated component
    // if (component instanceof ComponentActivation) {
    //   component.onActivate();
    // }
    //
    // // notify all listeners
    // this.componentActivationListeners.forEach((l) => l.onComponentActivated(this.currentActivatedComponent));
  }

  // registerComponentActivationListener(listener: ComponentActivationListener): void {
  //   this.componentActivationListeners.push(listener);
  // }

  // registerNavigationBackEnableListener(listener: NavigationBackEnableListener): void {
  //   this.navigationBackListeners.push(listener);
  // }
  //
  // set backEnabled(enabled: boolean) {
  //   this.navigationBackListeners.forEach(l => l.backEnabled = enabled);
  // }

  isActive(component: any): boolean {
    return this.componentCallbacks.isActive(component);
  }

  onActivate(component: any, callback: () => void): void {
    this.componentCallbacks.getCallback(component).activate.subscribe(() => {
      let a = 0;
      callback();
    });
  }
}

import {Injectable} from '@angular/core';
import {TitlebarComponent} from './titlebar/titlebar.component';
import {Subject} from 'rxjs';
import {StatusbarComponent} from './statusbar/statusbar.component';
import {Fov} from './panorama-screen/panorama-screen.component';

@Injectable({
  providedIn: 'root'
})
export class StateService {
  _titlebar?: TitlebarComponent;
  _statusbar?: StatusbarComponent;
  _currentRouterComponent = new Subject<any>();
  private _panoFov?: Fov;
  private _pictureFov?: Fov;
  address = '127.0.0.1';

  constructor() {
    this._titlebar = undefined;
  }

  set titlebar(titlebar: TitlebarComponent) {
    this._titlebar = titlebar;
  }

  set statusbar(statusbar: StatusbarComponent) {
    this._statusbar = statusbar;
  }

  set title(title: string) {
    if (this._titlebar) {
      this._titlebar.title = title;
    }
  }

  set backDisabled(disabled: boolean) {
    if (this._titlebar) {
      this._titlebar.backDisabled = disabled;
    }
  }

  set status(status: string) {
    if (this._statusbar) {
      this._statusbar.status = status;
    }
  }

  set currentRouterComponent(currentRouterComponent: any) {
    this._currentRouterComponent.next(currentRouterComponent);
  }

  get currentRouterComponentSubject(): Subject<any> {
    return this._currentRouterComponent;
  }


  get panoFov(): undefined | Fov {
    return this._panoFov;
  }

  set panoFov(value: undefined | Fov) {
    this._panoFov = value;
  }


  get pictureFov(): undefined | Fov {
    return this._pictureFov;
  }

  set pictureFov(value: undefined | Fov) {
    this._pictureFov = value;
  }
}

import {Injectable} from '@angular/core';
import {TitlebarComponent} from './titlebar/titlebar.component';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TitlebarService {
  private _titlebarComponent?: TitlebarComponent;

  constructor() {
  }

  set titlebar(value: TitlebarComponent) {
    this._titlebarComponent = value;
  }

  set backEnabled(enabled: boolean) {
    if (this._titlebarComponent) {
      this._titlebarComponent.backEnabled = enabled;
    }
  }

  set saveEnabled(enabled: boolean) {
    if (this._titlebarComponent) {
      this._titlebarComponent.saveEnabled = enabled;
    }
  }

  get backObservable(): Observable<void> | undefined {
    if (this._titlebarComponent) {
      return this._titlebarComponent.backObservable;
    }
    return undefined;
  }

  get saveObservable(): Observable<void> | undefined {
    if (this._titlebarComponent) {
      return this._titlebarComponent.saveObservable;
    }
    return undefined;
  }

  set title(title: string) {
    if (this._titlebarComponent) {
      this._titlebarComponent.title = title;
    }
  }

  onSave(callback: () => void): void {
    if (this._titlebarComponent) {
      this._titlebarComponent.saveObservable.subscribe(callback);
    }
  }

  onBack(callback: () => void): void {
    if (this._titlebarComponent) {
      this._titlebarComponent.backObservable.subscribe(callback);
    }
  }
}

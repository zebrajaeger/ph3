import {Injectable} from '@angular/core';
import {ServerService} from './server.service';
import {Observable, Subject} from 'rxjs';

export interface PanoSettings {
  minOverlapX: number;
  minOverlapY: number;
}

export interface PanoAxis {
  n: number; // n images
  overlap: number; // overlap
}

export interface Pano {
  x: PanoAxis;
  y: PanoAxis;
}

export class Fov {
  x1: number | undefined;
  x2: number | undefined;
  y1: number | undefined;
  y2: number | undefined;
  partial = false;

  constructor(data?: Partial<Fov>) {
    if (data) {
      Object.assign(this, data);
    }
  }

  get w(): number | undefined {
    if (this.partial === true) {
      return 360;
    }

    if (this.x1 !== undefined && this.x2 !== undefined) {
      return Math.abs(this.x2 - this.x1);
    }
    return undefined;
  }

  get h(): number | undefined {
    if (this.y1 !== undefined && this.y2 !== undefined) {
      return Math.abs(this.y2 - this.y1);
    }
    return undefined;
  }
}

@Injectable({
  providedIn: 'root'
})
export class PanoService {
  private _panoSettings: Subject<PanoSettings> = new Subject();
  private _cameraFov: Subject<Fov> = new Subject();
  private _panoFov: Subject<Fov> = new Subject();
  private _pano: Subject<Pano> = new Subject();

  constructor(private server: ServerService) {
    server.isConnected.subscribe(isConnected => {
      if (isConnected) {
        this.server.value('panoSettings').subscribe(value => {
          this._panoSettings.next(value);
        });
        this.server.value('panoFov').subscribe(value => {
          this._panoFov.next(value);
        });
        this.server.value('cameraFov').subscribe(value => {
          this._cameraFov.next(value);
        });
        this.server.value('pano').subscribe(value => {
          this._pano.next(value);
        });
      }
    });
  }

  get panoSettingsObservable(): Observable<PanoSettings> {
    return this._panoSettings;
  }

  get cameraFovObservable(): Observable<Fov> {
    return this._cameraFov;
  }

  get panoFovObservable(): Observable<Fov> {
    return this._panoFov;
  }

  get panoObservable(): Observable<Pano> {
    return this._pano;
  }

  set panoSettings(value: PanoSettings) {
    this.server.notify('setPanoSettings', value).then();
  }

  set cameraFov(value: Fov) {
    this.server.notify('setCameraFov', value).then();
  }

  set panoFov(value: Fov) {
    this.server.notify('setCameraFov', value).then();
  }
}

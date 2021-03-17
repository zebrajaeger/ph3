import {Injectable} from '@angular/core';
import {ServerService} from './server.service';
import {Subject} from 'rxjs';
import {map} from 'rxjs/operators';

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
    if (this.partial !== true) {
      return undefined;
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
    this.server.subscribeToValue('panoSettings', value => {
      this._panoSettings.next(value);
    });
    this.server.subscribeToValue('panoFov', value => {
      this._panoFov.next(value);
    });
    this.server.subscribeToValue('cameraFov', value => {
      this._cameraFov.next(value);
    });
    this.server.subscribeToValue('pano', value => {
      this._pano.next(value);
    });
  }

  // pano-settings
  onPanoSettings(callback: (data: PanoSettings) => void): void {
    this._panoSettings.subscribe(callback);
  }

  requestPanoSettings(callback: (data: PanoSettings) => void): void {
    this.server.call('getPanoSettings')?.then(callback);
  }

  set panoSettings(value: PanoSettings) {
    this.server.notify('setPanoSettings', value)?.then();
  }


  // pano
  onPano(callback: (data: Pano) => void): void {
    this._pano.subscribe(callback);
  }


  // camera-fov
  onCameraFov(callback: (data: Fov) => void): void {
    this._cameraFov.pipe(map(fov => new Fov(fov))).subscribe(callback);
  }

  requestCameraFov(callback: (data: Fov) => void): void {
    this.server.call('getCameraFov')?.then(fov => callback(new Fov(fov)));
  }

  set cameraFov(value: Fov) {
    this.server.notify('setCameraFov', value)?.then();
  }

  // pano-fov
  onPanoFov(callback: (data: Fov) => void): void {
    this._panoFov.pipe(map(fov => new Fov(fov))).subscribe(callback);
  }

  requestPanoFov(callback: (data: Fov) => void): void {
    this.server.call('getPanoFov')?.then(fov => callback(new Fov(fov)));
  }

  set panoFov(value: Fov) {
    this.server.notify('setPanoFov', value)?.then();
  }
}

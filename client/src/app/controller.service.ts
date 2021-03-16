import {Injectable} from '@angular/core';
import {ServerService} from './server.service';
import {Subject} from 'rxjs';

// {
//   status: { movement: 21, camera: 0 },
//   x: { pos: 0, speed: 0, atTargetPos: true, isMoving: false },
//   y: { pos: 0, speed: 0, atTargetPos: true, isMoving: false },
//   camera: { focus: false, trigger: false }
// }

export interface StatusData {
  movement: number;
  camera: number;
}

export interface MotorData {
  pos: number;
  speed: number;
  atTargetPos: boolean;
  isMoving: boolean;
}

export interface CameraData {
  focus: boolean;
  trigger: boolean;
}

export interface ControllerData {
  status: StatusData;
  x: MotorData;
  y: MotorData;
  camera: CameraData;
}

@Injectable({
  providedIn: 'root'
})
export class ControllerService {
  private _data = new Subject<ControllerData>();

  constructor(private server: ServerService) {
    this.server.subscribeToValue('controller', data => {
      this._data.next(data);
    });
  }

  onData(callback: (data: ControllerData) => void): void {
    this._data.subscribe(callback);
  }
}

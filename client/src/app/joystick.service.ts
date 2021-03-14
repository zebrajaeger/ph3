import {Injectable} from '@angular/core';
import {ServerService} from './server.service';
import {Observable, Observer, Subject} from 'rxjs';

// {
//   x: {
//     threshold: 0.05,
//       raw: { value: 494, min: 104, center: 495, max: 884 },
//     calculated: { value: 0.49872122762148335, capped: 0.5 }
//   },
//   y: {
//     threshold: 0.05,
//       raw: { value: 551, min: 104, center: 552, max: 975 },
//     calculated: { value: 0.49888392857142855, capped: 0.5 }
//   }
// }

export interface RawValue {
  value: number;
  min: number;
  center: number;
  max: number;
}

export interface CalculatedValue {
  value: number;
  capped: number;
}


export interface AxisData {
  threshold: number;
  raw: RawValue;
  calculated: CalculatedValue;
}

export interface JoystickData {
  x: AxisData;
  y: AxisData;
}

@Injectable({
  providedIn: 'root'
})
export class JoystickService {
  private mIsJogging = new Subject<boolean>();
  private mData = new Subject<JoystickData>();

  constructor(private server: ServerService) {
    server.isConnected.subscribe(isConnected => {
      if (isConnected) {
        this.server.subscribe('isJogging').subscribe(isJogging => {
          this.mIsJogging.next(isJogging);
        });
        this.server.call('getJogging', null).then(isJogging => {
          this.mIsJogging.next(isJogging);
        });
        this.server.subscribe('joystick').subscribe(data => {
          this.mData.next(data);
        });
      }
    });
  }

  get isJogging(): Observable<boolean> {
    return this.mIsJogging;
  }

  get data(): Subject<JoystickData> {
    return this.mData;
  }

  setJogging(value: boolean): Promise<boolean> {
    return this.server.call('setJogging', value === true);
  }

  resetCalibration(): Promise<any> {
    return this.server.call('setJoystickCalibrationReset', null);
  }

  saveCalibration(): Promise<any> {
    return this.server.call('setSaveJoystickCalibrationData', null);
  }
}


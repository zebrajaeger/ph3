import {Injectable} from '@angular/core';
import {ServerService} from './server.service';
import {Observable, Subject} from 'rxjs';

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

export interface Jogging {
  isJogging: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class JoystickService {
  private _isJogging = new Subject<Jogging>();
  private _data = new Subject<JoystickData>();

  constructor(private server: ServerService) {
    this.server.subscribeToValue('jogging', (isJogging: Jogging) => {
      this._isJogging.next(isJogging);
    });
    this.server.call('getJogging', null)?.then(isJogging => {
      this._isJogging.next(isJogging);
    });
    this.server.subscribeToValue('joystick', data => {
        this._data.next(data);
      }
    );
  }


  onJogging(callback: (data: Jogging) => void): void {
    this._isJogging.subscribe(callback);
  }

  onJoystickData(callback: (data: JoystickData) => void): void {
    this._data.subscribe(callback);
  }

  set jogging(jogging: Jogging) {
    this.server.notify('setJogging', jogging)?.then();
  }

  // setJogging(value: boolean): Promise<boolean> {
  //   return this.server.call('setJogging', value === true);
  // }
  //
  // resetCalibration(): Promise<any> {
  //   return this.server.call('setJoystickCalibrationReset', null);
  // }
  //
  // saveCalibration(): Promise<any> {
  //   return this.server.call('setSaveJoystickCalibrationData', null);
  // }
}


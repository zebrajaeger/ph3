import {Injectable} from '@angular/core';
import {BehaviorSubject, Subject} from 'rxjs';

export interface Axis {
  x: number;
  y: number;
}

@Injectable({
  providedIn: 'root'
})
export class DeviceService {
  _axis = new BehaviorSubject<Axis>({
    x: 0.1234,
    y: 2.345
  });

  get axis(): Axis {
    return this._axis.value;
  }

  set axis(axis: Axis) {
    this._axis.next(axis);
  }

  get axisSubject(): Subject<Axis> {
    return this._axis;
  }
}

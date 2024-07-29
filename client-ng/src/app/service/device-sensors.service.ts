import { Injectable } from '@angular/core';
import { Observable, fromEvent } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class DeviceSensorsService {

  constructor() { }
    isDeviceOrientationAvailable() : boolean {
        return !!window.DeviceOrientationEvent;
    }

    getDeviceOrientation(): Observable<DeviceOrientationEvent> {
      return fromEvent<DeviceOrientationEvent>(window, 'deviceorientation');
    }

    // getDeviceMotion(): Observable<DeviceMotionEvent> {
    //   return fromEvent<DeviceMotionEvent>(window, 'devicemotion');
    // }
}

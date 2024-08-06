import {Injectable} from '@angular/core';
import {RxStompService} from "./rx-stomp.service";
import {Gps} from "../../data/pano";
import {Subscription} from "rxjs";
import {map} from "rxjs/operators";
import {RxStompRPCService} from "./rx-stomp-rpc.service";

@Injectable({
    providedIn: 'root'
})
export class GpsService {

    constructor(private rxStompService: RxStompService, private rxStompRPCService: RxStompRPCService) {
    }

    subscribeGps(cb: (gps: Gps) => void): Subscription {
        return this.rxStompService
            .watch('/topic/gps')
            .pipe(map(msg => JSON.parse(msg.body) as Gps))
            .subscribe(cb);
    }

    requestGps(cb: (gps: Gps) => void): void {
        this.rxStompRPCService
            .rpc({destination: '/rpc/gps'})
            .pipe(map(msg => JSON.parse(msg.body) as Gps))
            .subscribe(cb);
    }

    startTracking() {
        if (navigator.geolocation) {
            console.log('Geolocation API available')
          navigator.geolocation.watchPosition(
            (position) => this.sendDevicePosition(position),
            (error) => console.error('Error getting GPS position', error),
            {
              enableHighAccuracy: true,
              timeout: 5000,
              maximumAge: 0
            }
          );
        } else {
          console.error('Geolocation is not supported by this browser.');
        }
      }
    
      private sendDevicePosition(position: GeolocationPosition): void {
        console.log('Got Position', position)
        const payload = {
          latitude: position.coords.latitude,
          longitude: position.coords.longitude,
          accuracy: position.coords.accuracy,
          altitude: position.coords.altitude,
          altitudeAccuracy: position.coords.altitudeAccuracy,
          heading: position.coords.heading,
          speed: position.coords.speed,
          timestamp: position.timestamp
        };
    
        this.rxStompService.publish({
          destination: '/gps',
          body: JSON.stringify(payload)
        });
      }
}

import {Pipe, PipeTransform} from '@angular/core';
import {Gps} from "../../data/pano";

@Pipe({
    name: 'gpsToString'
})
export class GpsToStringPipe implements PipeTransform {

    transform(gps: Gps | undefined): unknown {
        if (gps && gps.valid && gps.geoLocation) {
            if (!gps.valid.location && !gps.valid.altitude) {
                return '-, -, -';
            } else if (gps.valid.location && !gps.valid.altitude) {
                return `${gps.geoLocation.latitude.toFixed(4)}, ${gps.geoLocation.longitude.toFixed(4)}, -`;
            } else if (!gps.valid.location && gps.valid.altitude) {
                return `-, -, ${gps.geoLocation.altitude.toFixed(1)}`;
            } else {
                return `${gps.geoLocation.latitude.toFixed(4)}, ${gps.geoLocation.longitude.toFixed(4)}, ${gps.geoLocation.altitude.toFixed(1)}`;
            }
        }
        return null;
    }
}

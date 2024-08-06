import { Pipe, PipeTransform } from '@angular/core';
import { Gps } from "../../data/pano";

@Pipe({
    name: 'gpsToString'
})
export class GpsToStringPipe implements PipeTransform {

    transform(gps: Gps | undefined): unknown {
        if (!gps || !gps.geoLocation) {
            return '-, -, -';
        }

        const lat = gps.geoLocation.latitude ? gps.geoLocation.latitude.toFixed(4) : '-';
        const lng = gps.geoLocation.longitude ? gps.geoLocation.longitude.toFixed(4) : '-';
        if (gps.geoLocation.altitude) {
            return `${lat}, ${lng}, ${gps.geoLocation.altitude.toFixed(1)}`;
        } else {
            return `${lat}, ${lng}`;
        }
    }
}

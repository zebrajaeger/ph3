import {Component, OnInit} from '@angular/core';
import {PanoService} from '../pano.service';

@Component({
    selector: 'app-record',
    templateUrl: './record.component.html',
    styleUrls: ['./record.component.scss']
})
export class RecordComponent implements OnInit {

    constructor(private panoService: PanoService) {
    }

    ngOnInit(): void {
    }

    onStart(): void {
    }

    onStop(): void {

    }

    onPause(): void {

    }
}

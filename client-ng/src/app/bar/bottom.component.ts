import {Component, OnInit} from '@angular/core';
import {PanoHeadService} from '../panohead.service';

@Component({
    selector: 'app-bottom',
    templateUrl: './bottom.component.html',
    styleUrls: ['./bottom.component.scss']
})
export class BottomComponent implements OnInit {
    public joystickPosition: string;

    constructor(private panoHeadService: PanoHeadService) {
        panoHeadService.actor().subscribe(actorData => {
            this.joystickPosition = `${actorData.x.pos.toFixed(3)}, ${actorData.y.pos.toFixed(3)}`;
        });
    }

    ngOnInit(): void {
    }

}

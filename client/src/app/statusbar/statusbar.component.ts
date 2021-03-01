import {Component, OnInit} from '@angular/core';
import {StateService} from '../state.service';
import {Axis, DeviceService} from '../device.service';

@Component({
  selector: 'app-statusbar',
  templateUrl: './statusbar.component.html',
  styleUrls: ['./statusbar.component.scss']
})
export class StatusbarComponent implements OnInit {

  connected = false;
  focus = false;
  trigger = true;
  movingX = true;
  movingY = false;
  _status = '';
  axis?: Axis;

  constructor(private dataConnectionService: DeviceService, private stateService: StateService) {
    stateService.statusbar = this;
    stateService.currentRouterComponentSubject.subscribe(screen => {
      this.status = '';
    });

    dataConnectionService.axisSubject.subscribe(axis => {
      this.axis = axis;
    });
  }

  ngOnInit(): void {
  }

  set status(status: string) {
    this._status = status;
  }
}

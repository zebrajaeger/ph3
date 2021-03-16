import {Component, OnInit} from '@angular/core';
import {Axis, DeviceService} from '../device.service';
import {ControllerData, ControllerService} from '../controller.service';
import {ServerService} from '../server.service';

@Component({
  selector: 'app-statusbar',
  templateUrl: './statusbar.component.html',
  styleUrls: ['./statusbar.component.scss']
})
export class StatusbarComponent implements OnInit {

  isConnected = false;
  status = '';
  axis?: Axis;
  controllerData?: ControllerData;

  constructor(private serverService: ServerService,
              private controllerService: ControllerService) {

    serverService.onConnectionChanged(isConnected => this.isConnected = isConnected);
    controllerService.onData(data => this.controllerData = data);
  }

  ngOnInit(): void {
  }
}

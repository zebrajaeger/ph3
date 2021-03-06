import {Component, OnInit} from '@angular/core';
import {StateService} from '../state.service';
import {DeviceService} from '../device.service';

@Component({
  selector: 'app-server-settings-screen',
  templateUrl: './server-settings-screen.component.html',
  styleUrls: ['./server-settings-screen.component.scss']
})
export class ServerSettingsScreenComponent implements OnInit {

  constructor(private dataConnectionService: DeviceService, public stateService: StateService) {
    const self = this;
    stateService.currentRouterComponentSubject.subscribe(screen => {
      if (screen === self) {
        stateService.title = 'Server Settings';
        stateService.backDisabled = false;
      }
    });
  }
  ngOnInit(): void {
  }

}

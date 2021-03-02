import {Component} from '@angular/core';
import {DeviceService} from '../device.service';
import {StateService} from '../state.service';

@Component({
  selector: 'app-play-screen',
  templateUrl: './play-screen.component.html',
  styleUrls: ['./play-screen.component.scss']
})
export class PlayScreenComponent {

  constructor(private dataConnectionService: DeviceService, private stateService: StateService) {
    const self = this;
    stateService.currentRouterComponentSubject.subscribe(screen => {
      if (screen === self) {
        stateService.title = 'Play';
        stateService.backDisabled = false;
      }
    });
  }

  onPlayPause(): void {

  }

  onStop(): void {

  }
}

import {Component} from '@angular/core';
import {DeviceService} from '../device.service';
import {StatusBarTitle} from '../titlebar/titlebar.component';
import {RouterService} from '../router.service';
import {TitlebarService} from '../titlebar.service';

@Component({
  selector: 'app-play-screen',
  templateUrl: './play-screen.component.html',
  styleUrls: ['./play-screen.component.scss']
})
export class PlayScreenComponent {

  constructor(private deviceService: DeviceService,
              private routerService: RouterService,
              private titlebarService: TitlebarService) {
    routerService.onActivate(this, () => {
      titlebarService.title = 'Play';
      titlebarService.backEnabled = true;
      titlebarService.saveEnabled = false;
    });
  }

  onPlayPause(): void {

  }

  onStop(): void {

  }

}

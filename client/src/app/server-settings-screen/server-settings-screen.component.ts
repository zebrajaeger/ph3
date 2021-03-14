import {Component, OnInit} from '@angular/core';
import {StatusBarTitle} from '../titlebar/titlebar.component';
import {DeviceService} from '../device.service';
import {RouterService} from '../router.service';
import {TitlebarService} from '../titlebar.service';

@Component({
  selector: 'app-server-settings-screen',
  templateUrl: './server-settings-screen.component.html',
  styleUrls: ['./server-settings-screen.component.scss']
})
export class ServerSettingsScreenComponent implements OnInit {

  address = '127.0.0.1';

  constructor(private deviceService: DeviceService,
              private routerService: RouterService,
              private titlebarService: TitlebarService) {
    routerService.onActivate(this, () => {
      titlebarService.title = 'Server Settings';
      titlebarService.backEnabled = true;
      titlebarService.saveEnabled = true;
    });
  }

  ngOnInit(): void {
  }

}

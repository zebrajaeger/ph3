import { Component } from '@angular/core';
import {SystemService} from "../../service/system.service";
import {RouterService} from "../../service/router.service";
import {UiService} from "../../service/ui.service";

@Component({
  selector: 'app-shutdown',
  templateUrl: './shutdown.component.html',
  styleUrls: ['./shutdown.component.scss']
})
export class ShutdownComponent {
  constructor(private routerService: RouterService,
              private uiService: UiService,
              private systemService: SystemService) {
    routerService.onActivate(this, () => this.onActivate());
  }

  private onActivate(): void {
    this.uiService.title.next('Shutdown');
    this.uiService.backButton.next(true);
  }
  shutdown(): void {
    this.systemService.shutdown();
    console.log('shutdown');
  }

  reboot(): void {
    this.systemService.reboot();
    console.log('reboot');
  }

  restartApp(): void {
    this.systemService.restartApp();
    console.log('restartApp');
  }
}

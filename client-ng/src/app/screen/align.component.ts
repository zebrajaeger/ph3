import {Component} from '@angular/core';
import {JoystickService} from '../joystick.service';
import {RouterService} from '../router.service';
import {UiService} from '../ui.service';
import {PanoHeadService} from '../panohead.service';
import {PanoService} from '../pano.service';

@Component({
  selector: 'app-align',
  templateUrl: './align.component.html',
  styleUrls: ['./align.component.scss']
})
export class AlignComponent {
  constructor(private routerService: RouterService,
              private joystickService: JoystickService,
              private panoHeadService: PanoHeadService,
              private panoService: PanoService,
              private uiService: UiService) {
    routerService.onActivate(this, () => this.onActivate());
  }

  private onActivate(): void {
    this.uiService.title.next('Align Panohead');
    this.uiService.backButton.next(true);
    this.panoHeadService.sendJogging(true);
    this.panoService.requestRecalculatePano();
  }

  setAsZero(): void {
    this.panoHeadService.sendSetToZero();
  }

  goToZero(): void {
    this.panoHeadService.sendGoToZero();
  }
}

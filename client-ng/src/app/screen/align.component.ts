import {Component} from '@angular/core';
import {JoystickService} from '../service/joystick.service';
import {RouterService} from '../service/router.service';
import {UiService} from '../service/ui.service';
import {PanoHeadService} from '../service/panohead.service';
import {PanoService} from '../service/pano.service';

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
  adaptOffset(): void {
    this.panoHeadService.adaptOffset();
  }
}

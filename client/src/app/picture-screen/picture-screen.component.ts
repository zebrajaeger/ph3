import {Component, OnInit} from '@angular/core';
import {Border} from '../border-chooser/border-chooser.component';
import {RouterService} from '../router.service';
import {TitlebarService} from '../titlebar.service';
import {Fov, PanoService} from '../pano.service';
import {Axis, DeviceService} from '../device.service';

@Component({
  selector: 'app-picture-screen',
  templateUrl: './picture-screen.component.html',
  styleUrls: ['./picture-screen.component.scss']
})
export class PictureScreenComponent {

  labels = {
    top: '-', right: '-', bottom: '-', left: '-'
  };

  fov = new Fov();

  constructor(private routerService: RouterService,
              private titlebarService: TitlebarService,
              private panoService: PanoService,
              private deviceService: DeviceService) {

    routerService.onActivate(this, () => {
      titlebarService.title = 'Picture Bounds';
      titlebarService.backEnabled = true;
      titlebarService.saveEnabled = true;
    });

    titlebarService.onSave(() => {
      if (routerService.isActive(this)) {
        panoService.cameraFov = this.fov;
      }
    });
  }

  onBorderChange(border: Border): void {
    let value;
    switch (border) {
      case Border.Top:
        value = this.deviceService.axis.y;
        this.fov.y1 = value;
        this.labels.top = value.toFixed(2);
        break;
      case Border.Bottom:
        value = this.deviceService.axis.y;
        this.fov.y2 = value;
        this.labels.bottom = value.toFixed(2);
        break;
      case Border.Left:
        value = this.deviceService.axis.x;
        this.fov.x1 = value;
        this.labels.left = value.toFixed(2);
        break;
      case Border.Right:
        value = this.deviceService.axis.x;
        this.fov.x2 = value;
        this.labels.right = value.toFixed(2);
        break;
    }
  }
}

import {Component, OnDestroy, OnInit} from '@angular/core';
import {Border} from '../border-chooser/border-chooser.component';
import {Axis, DeviceService} from '../device.service';
import {StatusBarTitle} from '../titlebar/titlebar.component';
import {Fov} from '../pano.service';
import {RouterService} from '../router.service';
import {TitlebarService} from '../titlebar.service';

@Component({
  selector: 'app-panorama-screen',
  templateUrl: './panorama-screen.component.html',
  styleUrls: ['./panorama-screen.component.scss']
})
export class PanoramaScreenComponent implements OnInit {

  labels = {
    top: '-', right: '-', bottom: '-', left: '-'
  };

  fov = new Fov({partial: true});

  constructor(private deviceService: DeviceService,
              private routerService: RouterService,
              private titlebarService: TitlebarService) {
    routerService.onActivate(this, () => {
      titlebarService.title = 'Pano bounds';
      titlebarService.backEnabled = true;
      titlebarService.saveEnabled = true;
    });
  }

  ngOnInit(): void {
  }

  set partial(partial: boolean) {
    this.fov.partial = partial;
  }

  get partial(): boolean {
    return this.fov.partial;
  }

  onPartialChange(partial: boolean): void {
    this.partial = partial;
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

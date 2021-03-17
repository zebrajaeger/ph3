import {Component, OnDestroy, OnInit} from '@angular/core';
import {Border} from '../border-chooser/border-chooser.component';
import {Axis, DeviceService} from '../device.service';
import {StatusBarTitle} from '../titlebar/titlebar.component';
import {Fov, PanoService} from '../pano.service';
import {RouterService} from '../router.service';
import {TitlebarService} from '../titlebar.service';
import {JoystickService} from '../joystick.service';
import {ControllerData, ControllerService} from '../controller.service';

@Component({
  selector: 'app-panorama-screen',
  templateUrl: './panorama-screen.component.html',
  styleUrls: ['./panorama-screen.component.scss']
})
export class PanoramaScreenComponent implements OnInit {

  private _fov = new Fov({partial: true});
  private _data?: ControllerData;
  private _labels = {
    top: '-', right: '-', bottom: '-', left: '-'
  };

  constructor(private routerService: RouterService,
              private titlebarService: TitlebarService,
              private panoService: PanoService,
              private controllerService: ControllerService,
              private joystickService: JoystickService) {

    routerService.onActivate(this, () => {
      titlebarService.title = 'Pano bounds';
      titlebarService.backEnabled = true;
      titlebarService.saveEnabled = true;
      joystickService.jogging = {isJogging: true};
    });

    routerService.onDeactivate(this, () => {
      joystickService.jogging = {isJogging: false};
    });

    titlebarService.onSave(() => {
      if (routerService.isActive(this)) {
        panoService.panoFov = this._fov;
      }
    });

    panoService.onPanoFov(fov => this.fov = fov);
    panoService.requestPanoFov(fov => this.fov = fov);

    controllerService.onData(data => this._data = data);
  }

  ngOnInit(): void {
  }

  set partial(partial: boolean) {
    this._fov.partial = partial;
  }

  get partial(): boolean {
    return this._fov.partial;
  }

  onPartialChange(partial: boolean): void {
    this.partial = partial;
  }


  get data(): ControllerData|undefined {
    return this._data;
  }

  onBorderChange(border: Border): void {
    let value;
    switch (border) {
      case Border.Top:
        if (this._data) {
          value = this._data?.y.pos;
          this._fov.y1 = value;
        }
        break;
      case Border.Bottom:
        if (this._data) {
          value = this._data?.y.pos;
          this._fov.y2 = value;
        }
        break;
      case Border.Left:
        if (this._data) {
          value = this._data?.x.pos;
          this._fov.x1 = value;
        }
        break;
      case Border.Right:
        if (this._data) {
          value = this._data?.x.pos;
          this._fov.x2 = value;
        }
        break;
    }
    this.updateLabels();
  }

  updateLabels(): void {
    this._labels.top = this.fov.y1?.toFixed(2) || '-';
    this._labels.bottom = this.fov.y2?.toFixed(2) || '-';
    this._labels.left = this.fov.x1?.toFixed(2) || '-';
    this._labels.right = this.fov.x2?.toFixed(2) || '-';
  }

  get labels(): { top: string; left: string; bottom: string; right: string } {
    return this._labels;
  }

  get fov(): Fov {
    return this._fov;
  }

  set fov(value: Fov) {
    this._fov = value;
    this.updateLabels();
  }
}

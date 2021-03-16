import {Component, OnInit} from '@angular/core';
import {Border} from '../border-chooser/border-chooser.component';
import {RouterService} from '../router.service';
import {TitlebarService} from '../titlebar.service';
import {Fov, PanoService} from '../pano.service';
import {ControllerData, ControllerService} from '../controller.service';
import {JoystickService} from '../joystick.service';

@Component({
  selector: 'app-picture-screen',
  templateUrl: './picture-screen.component.html',
  styleUrls: ['./picture-screen.component.scss']
})
export class PictureScreenComponent implements OnInit {

  private _fov = new Fov();
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
      titlebarService.title = 'Picture Bounds';
      titlebarService.backEnabled = true;
      titlebarService.saveEnabled = true;
      joystickService.jogging = {isJogging: true};
    });

    routerService.onDeactivate(this, () => {
      joystickService.jogging = {isJogging: false};
    });

    titlebarService.onSave(() => {
      if (routerService.isActive(this)) {
        panoService.cameraFov = this._fov;
      }
    });

    panoService.onCameraFov(fov => this.fov = fov);
    panoService.requestCameraFov(fov => this.fov = fov);

    controllerService.onData(data => this._data = data);
  }

  ngOnInit(): void {
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

  get fov(): Fov {
    return this._fov;
  }


  set fov(value: Fov) {
    this._fov = value;
    this.updateLabels();
  }

  get labels(): { top: string; left: string; bottom: string; right: string } {
    return this._labels;
  }
}

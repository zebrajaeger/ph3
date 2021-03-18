import {Component} from '@angular/core';
import {DeviceService} from '../device.service';
import {RouterService} from '../router.service';
import {TitlebarService} from '../titlebar.service';
import {Pano, PanoService} from '../pano.service';

class PanoHeadPos {
  private _x = 0;
  private _y = 0;

  constructor(data?: Partial<PanoHeadPos>) {
    if (data) {
      Object.assign(this, data);
    }
  }

  get x(): number {
    return this._x;
  }

  set x(value: number) {
    this._x = value;
  }

  get y(): number {
    return this._y;
  }

  set y(value: number) {
    this._y = value;
  }
}

@Component({
  selector: 'app-play-screen',
  templateUrl: './play-screen.component.html',
  styleUrls: ['./play-screen.component.scss']
})
export class PlayScreenComponent {

  private _pano?: Pano;
  private _panoHeadPos = new PanoHeadPos();

  constructor(private deviceService: DeviceService,
              private routerService: RouterService,
              private panoService: PanoService,
              private titlebarService: TitlebarService) {
    routerService.onActivate(this, () => {
      titlebarService.title = 'Play';
      titlebarService.backEnabled = true;
      titlebarService.saveEnabled = false;
    });

    panoService.onPano(pano => this._pano = pano);
    panoService.requestPano(pano => {
      this._pano = pano;
    });
  }

  onPlayPause(): void {

  }

  onStop(): void {

  }


  get pano(): Pano | undefined {
    return this._pano;
  }

  get panoHeadPos(): PanoHeadPos {
    return this._panoHeadPos;
  }
}

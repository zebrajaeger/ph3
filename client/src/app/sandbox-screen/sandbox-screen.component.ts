import {Component, OnInit} from '@angular/core';
import {Fov, Pano, PanoService} from '../pano.service';
import {DeviceService} from '../device.service';
import {RouterService} from '../router.service';
import {TitlebarService} from '../titlebar.service';
import {Jogging, JoystickData, JoystickService} from '../joystick.service';

@Component({
  selector: 'app-sandbox-screen',
  templateUrl: './sandbox-screen.component.html',
  styleUrls: ['./sandbox-screen.component.scss']
})
export class SandboxScreenComponent implements OnInit {

  pano?: Pano;
  cameraFov?: Fov;
  panoFov?: Fov;
  _joystick?: JoystickData;
  private _jogging?: Jogging;

  constructor(private deviceService: DeviceService,
              private routerService: RouterService,
              private titlebarService: TitlebarService,
              private panoService: PanoService,
              private joystickService: JoystickService) {
    routerService.onActivate(this, () => {
      titlebarService.title = 'Sandbox';
      titlebarService.backEnabled = true;
      titlebarService.saveEnabled = false;
    });
    // panoService.panoObservable.subscribe(pano => {
    //   this.pano = pano;
    // });
    // panoService.cameraFovObservable.subscribe(fov => {
    //   this.cameraFov = fov;
    // });
    // panoService.panoFovObservable.subscribe(fov => {
    //   this.panoFov = fov;
    // });
    joystickService.onJoystickData(data => {
      this._joystick = data;
    });
    joystickService.onJogging(data => {
      this._jogging = data;
    });
  }

  ngOnInit(): void {
  }

  onTest(): void {
    this.joystickService.jogging = {isJogging: !this.isJogging};
  }

  get isJogging(): boolean {
    return this._jogging?.isJogging || false;
  }
}

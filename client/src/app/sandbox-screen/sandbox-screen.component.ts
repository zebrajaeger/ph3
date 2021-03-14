import {Component, OnInit} from '@angular/core';
import {Fov, Pano, PanoService} from '../pano.service';
import {DeviceService} from '../device.service';
import {RouterService} from '../router.service';
import {TitlebarService} from '../titlebar.service';

@Component({
  selector: 'app-sandbox-screen',
  templateUrl: './sandbox-screen.component.html',
  styleUrls: ['./sandbox-screen.component.scss']
})
export class SandboxScreenComponent implements OnInit {

  pano?: Pano;
  cameraFov?: Fov;
  panoFov?: Fov;

  constructor(private deviceService: DeviceService,
              private routerService: RouterService,
              private titlebarService: TitlebarService,
              private panoService: PanoService) {
    routerService.onActivate(this, () => {
      titlebarService.title = 'Play';
      titlebarService.backEnabled = true;
      titlebarService.saveEnabled = false;
    });
    panoService.panoObservable.subscribe(pano => {
      this.pano = pano;
    });
    panoService.cameraFovObservable.subscribe(fov => {
      this.cameraFov = fov;
    });
    panoService.panoFovObservable.subscribe(fov => {
      this.panoFov = fov;
    });
  }

  ngOnInit(): void {
  }

  onTest(): void {
    this.panoService.cameraFov = new Fov({x1: 1, x2: 2, y1: 3, y2: 4});
  }
}

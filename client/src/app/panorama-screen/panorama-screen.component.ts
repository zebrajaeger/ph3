import {Component, OnInit} from '@angular/core';
import {StateService} from '../state.service';
import {Border} from '../border-chooser/border-chooser.component';
import {Axis, DeviceService} from '../device.service';

export interface Fov {
  top?: number;
  right?: number;
  bottom?: number;
  left?: number;
  partial: boolean;
}

@Component({
  selector: 'app-panorama-screen',
  templateUrl: './panorama-screen.component.html',
  styleUrls: ['./panorama-screen.component.scss']
})
export class PanoramaScreenComponent {

  labels = {
    top: '-', right: '-', bottom: '-', left: '-'
  };

  fov: Fov = {bottom: undefined, left: undefined, right: undefined, top: undefined, partial: true};

  axis?: Axis;

  w?: number;
  h?: number;

  constructor(private dataConnectionService: DeviceService, private stateService: StateService) {
    const self = this;
    stateService.currentRouterComponentSubject.subscribe(screen => {
      if (screen === self) {
        stateService.title = 'Panorama Bounds';
        stateService.backDisabled = false;
      }
    });

    dataConnectionService.axisSubject.subscribe(axis => {
      this.axis = axis;
    });
  }

  set partial(partial: boolean) {
    this.fov.partial = partial;
  }

  get partial(): boolean {
    return this.fov.partial;
  }

  onPartialChange(partial: boolean): void {
    this.partial = partial;
    this.updateWH();
  }

  onBorderChange(border: Border): void {
    switch (border) {
      case Border.Top:
        if (this.axis?.y) {
          this.fov.top = this.axis.y;
          this.labels.top = this.fov.top.toFixed(2);
        }
        break;
      case Border.Bottom:
        if (this.axis?.y) {
          this.fov.bottom = this.axis.y;
          this.labels.bottom = this.fov.bottom.toFixed(2);
        }
        break;
      case Border.Left:
        if (this.axis?.x) {
          this.fov.left = this.axis.x;
          this.labels.left = this.fov.left.toFixed(2);
        }
        break;
      case Border.Right:
        if (this.axis?.x) {
          this.fov.right = this.axis.x;
          this.labels.right = this.fov.right.toFixed(2);
        }
        break;
    }
    this.updateWH();
  }

  updateWH(): void {
    if (this.fov.partial) {
      if (this.fov.left !== undefined && this.fov.right !== undefined) {
        this.w = Math.abs(this.fov.left - this.fov.right); // TODO consider more than one revolution
      } else {
        this.w = undefined;
      }
    } else {
      this.w = 360.0;
    }

    if (this.fov.top !== undefined && this.fov.bottom !== undefined) {
      this.h = Math.abs(this.fov.top - this.fov.bottom); // TODO consider more than one revolution
    } else {
      this.h = undefined;
    }

    this.stateService.panoFov = this.fov;
  }
}

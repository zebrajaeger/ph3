import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { PanoHeadService } from "../service/panohead.service";
import { Position } from "../../data/panohead";
import { DeviceSensorsService } from '../service/device-sensors.service';
import { JoystickService } from '../service/joystick.service';

@Component({
  selector: 'movement-controller',
  templateUrl: './movement-controller.component.html',
  styleUrls: ['./movement-controller.component.scss']
})
export class MovementControllerComponent implements OnInit, OnDestroy {
  @Input()
  public timerPeriodMs = 80;

  private timer: any | undefined;

  // buttons
  public buttonOpacity = '1';

  // joystick
  public showJoystick = false;
  public joystickActive = false;
  private joystickThreshold = 0.2;
  private joystickPosition?: Position;
  // public joystickOpacity = '0.2';

  // touchpad
  public showTouchpad = false;
  private touchpadSumX = 0;
  private touchpadSumY = 0;

  constructor(private panoHeadService: PanoHeadService, private joystickService: JoystickService) {
  }

  ngOnInit(): void {
    this.timer = setInterval(() => this.onTimer(), this.timerPeriodMs);
  }

  onTimer(): void {
    if (this.showJoystick) {
      if (this.joystickPosition) {
        this.joystickService.sendDevicePosition(this.joystickPosition);
      }
    }

    if (this.showTouchpad) {
      this.panoHeadService.sendManualMove(new Position(this.touchpadSumX, this.touchpadSumY))
      this.touchpadSumX = 0;
      this.touchpadSumY = 0;
    }
  }

  ngOnDestroy(): void {
    if (this.timer) {
      console.log('stop timer');
      clearInterval(this.timer);
      this.timer = null;
    }
  }

  onManualMove(x: number, y: number): void {
    if (!this.showJoystick && !this.showTouchpad) {
      this.panoHeadService.sendManualMoveForced(new Position(x, y));
    }
  }

  onTouchpadPosition(relPos: Position): void {
    this.touchpadSumX += relPos.x;
    this.touchpadSumY += relPos.y;
  }

  onJoystickPosition(pos: Position): void {
    pos.x = this.mapAngel(pos.x);
    pos.y = this.mapAngel(pos.y);
    this.joystickActive = (pos.x !== 0) || (pos.y !== 0);
  }

  mapAngel(a: number): number {
    if (a >= this.joystickThreshold) {
      return this.map(a, this.joystickThreshold, 1, 0, 1);
    } else if (a <= -this.joystickThreshold) {
      return this.map(a, -1, -this.joystickThreshold, -1, 0);
    } else {
      return 0;
    }
  }

  map(v: number, in_min: number, in_max: number, out_min: number, out_max: number) {
    return (v - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
  }

  closeTouchpad() {
    this.showTouchpad = false;
  }

  closeJoystick() {
    this.showJoystick = false;
  }

  toggleDeviceOrientationMove(): void {
    this.showTouchpad = !this.showTouchpad;
    // this.showJoystick = !this.showJoystick;
  }
}

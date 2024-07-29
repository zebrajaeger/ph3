import { Component, EventEmitter, HostListener, Input, OnInit, Output } from '@angular/core';
import { DeviceSensorsService } from 'src/app/service/device-sensors.service';
import { Position } from 'src/data/panohead';

@Component({
  selector: 'app-device-orientation-joystick',
  templateUrl: './device-orientation-joystick.component.html',
  styleUrls: ['./device-orientation-joystick.component.scss']
})
export class DeviceOrientationJoystickComponent implements OnInit {
  @Input() doubleTabMaxTime = 300;
  @Input() handleRadius = 200;
  @Input() active = false;

  @Output() position = new EventEmitter<Position>();
  @Output() doubleTap = new EventEmitter<void>();

  public handleTransform: String = 'translate(0px, 0px)';
  public deviceOrientationAvailable: boolean = false;
  public x = 0;
  public y = 0;

  private lastTap = 0;
  private tapTimeout: any;

  constructor(private deviceSensorService: DeviceSensorsService) {
  }

  ngOnInit(): void {
    if (this.deviceSensorService.isDeviceOrientationAvailable()) {
      this.deviceOrientationAvailable = true;

      this.deviceSensorService.getDeviceOrientation().subscribe(e => {
        if (e.gamma === null || e.beta === null) {
          return;
        }
        // console.log('here3!')
        this.x = e.gamma / 90;
        this.y = e.beta / 90;
        this.position.emit(new Position(this.x, this.y));
        this.handleTransform = `translate(${this.x * this.handleRadius}px, ${this.y * this.handleRadius}px)`;
      });
    }
  }

  @HostListener('dblclick') onDoubleClick() {
    this.doubleTap.emit();
  }

  @HostListener('touchend', ['$event']) onTouchEnd(event: TouchEvent) {
    event.preventDefault();
    const now = Date.now();
    const time = now - this.lastTap;

    // Check if the time between taps is less than 300ms    
    if (time < this.doubleTabMaxTime && time > 0) {
      // Double-tap detected
      this.doubleTap.emit();
    }
    this.lastTap = now;

    // Clear the previous timeout if any
    clearTimeout(this.tapTimeout);
    // Set a timeout to reset lastTap after 300ms
    this.tapTimeout = setTimeout(() => {
      this.lastTap = 0;
    }, this.doubleTabMaxTime);
  }
}

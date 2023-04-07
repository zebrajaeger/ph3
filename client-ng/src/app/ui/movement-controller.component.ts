import {Component} from '@angular/core';
import {PanoHeadService} from "../panohead.service";
import {Position} from "../../data/panohead";

@Component({
  selector: 'movement-controller',
  templateUrl: './movement-controller.component.html',
  styleUrls: ['./movement-controller.component.scss']
})
export class MovementControllerComponent {
  private timer;
  private joystickPos: Position = {x: 0, y: 0};

  constructor(private panoHeadService: PanoHeadService) {
  }

  manualMove(x: number, y: number): void {
    this.panoHeadService.sendManualMove({x, y})
  }

  onMove(e: any): void {
    this.joystickPos = e.data.vector;
  }

  onJoystickStart() {
    console.log('start');
    this.timer = setInterval(_ => {
      this.panoHeadService.sendManualMoveByJoystick(this.joystickPos)
    })
  }

  onJoystickEnd() {
    console.log('end');
    clearInterval(this.timer);
    this.timer = undefined;
    this.panoHeadService.sendManualMoveByJoystickStop()
  }
}

import {
  AfterViewInit,
  Component,
  ElementRef,
  Input,
  OnChanges,
  OnDestroy,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import {CameraOfView, PanoFieldOfView, PanoMatrix} from "../../data/pano";
import {PanoService} from "../service/pano.service";
import {Subscription} from "rxjs";
import {AutomateState, RobotState} from "../../data/record";
import {PanoHeadService} from "../service/panohead.service";

@Component({
  selector: 'matrix2',
  templateUrl: './matrix2.component.html',
  styleUrls: ['./matrix2.component.scss']
})
export class Matrix2Component implements AfterViewInit, OnChanges, OnDestroy {
  @Input()
  public width!: number;
  @Input()
  public height!: number;
  @Input()
  public color?: string;
  @ViewChild('matrix2', {static: true})
  canvas!: ElementRef<HTMLCanvasElement>;

  public pictureFov_!: CameraOfView;
  private pictureFovSubscription!: Subscription;

  public panoFov_!: PanoFieldOfView;
  private panoFovSubscription!: Subscription;

  public panoMatrix_!: PanoMatrix;
  private panoMatrixSubscription!: Subscription;

  private robotStateSubscription!: Subscription;
  public _robotState!: RobotState;

  constructor(private panoService: PanoService,
              private panoHeadService: PanoHeadService,) {
    this.pictureFovSubscription = this.panoService.subscribePictureFov(fov => this.pictureFov = fov);
    this.panoService.requestPictureFov(fov => this.pictureFov = fov);

    this.panoFovSubscription = this.panoService.subscribePanoFov(fov => this.panoFov = fov);
    this.panoService.requestPanoFov(fov => this.panoFov = fov);

    this.panoFovSubscription = this.panoService.subscribePanoMatrix(matrix => this.panoMatrix = matrix);
    this.panoService.requestPanoMatrix(matrix => this.panoMatrix = matrix);

    this.robotStateSubscription = this.panoHeadService.subscribeRobotState(robotState => this.robotState = robotState);
  }

  ngOnDestroy(): void {
    this.panoFovSubscription?.unsubscribe();
    this.pictureFovSubscription?.unsubscribe();
    this.panoMatrixSubscription?.unsubscribe();
    this.robotStateSubscription?.unsubscribe();
  }

  ngAfterViewInit(): void {
    this.draw();
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.draw();
  }

  set pictureFov(fov: CameraOfView) {
    this.pictureFov_ = fov;
    this.pictureFov_.normalizeDirection();
    this.draw();
  }

  set panoFov(fov: PanoFieldOfView) {
    this.panoFov_ = fov;
    this.panoFov_.normalizeDirection();
    this.draw();
  }

  set panoMatrix(matrix: PanoMatrix) {
    this.panoMatrix_ = matrix;
    this.draw();
  }

  set robotState(value: RobotState) {
    this._robotState = value;
    this.draw();
  }

  private normalizeAndConvertX(deg: number): number {
    const l = deg/360; //[1...-1]
    return this.width/2 + this.width*l;
  }

  private normalizeAndConvertY(deg: number): number {
    deg = (deg + 90) % 180;
    if (deg < 0) {
      deg += 180;
    }
    return deg * this.height / 180;
  }

  private drawEllipse(
      ctx: CanvasRenderingContext2D,
      x: number, y: number,
      w: number, h: number,
      fillStyle: string,
      i: number) {

    ctx.beginPath();
    ctx.fillStyle = fillStyle;
    ctx.strokeStyle = 'black';
    ctx.lineWidth = 1;
    ctx.ellipse(x, y, w / 2, h / 2, 0, 0, 360);
    ctx.fill();
    ctx.stroke();

    const text = `${i}`;
    const metrics = ctx.measureText(text);
    const textH = metrics.actualBoundingBoxAscent + metrics.actualBoundingBoxDescent;
    const textW = metrics.width;

    ctx.fillStyle = 'black'
    ctx.fillText(`${i}`, x - textW / 2, y + textH / 2);
  }

  private draw() {
    const ctx = this.canvas.nativeElement.getContext("2d");
    if (ctx === null) {
      return;
    }

    ctx.clearRect(0, 0, this.width, this.height);
    this.drawPanoFov(ctx);
    this.drawPictures(ctx);
    this.drawZeroCross(ctx);
    ctx.beginPath();
  }

  private drawPictures(ctx: CanvasRenderingContext2D) {
    if (this.panoMatrix_) {
      let camX;
      let camY;
      if (this.pictureFov_
          && this.pictureFov_.x && this.pictureFov_.x.complete
          && this.pictureFov_.y&& this.pictureFov_.y.complete) {
        let ax = this.pictureFov_.x.size;
        let ay = this.pictureFov_.y.size;
        camX = <number>ax * this.width / 360;
        camY = <number>ay * this.height / 180;
      } else {
        camX = 20;
        camY = 20;
      }

      let imgIndex = this._robotState?.command?.shotPosition?.index;
      if (imgIndex == null) {
        imgIndex = -1;
      }

      let isShooting = this._robotState?.automateState == AutomateState.CMD_SHOT;
      let i = 0;
      ctx.lineWidth = 2;
      ctx.font = "bold 15px Roboto";
      ctx.stroke();
      for (let yi = 0; yi < this.panoMatrix_.ySize; ++yi) {
        const y = this.normalizeAndConvertY(this.panoMatrix_.yPositions[yi]);
        const row = this.panoMatrix_.xPositions[yi];
        for (let xi = 0; xi < row.length; ++xi) {
          let xx = row[xi] % 360;
          const x = this.normalizeAndConvertX(xx) % this.width;

          let fill;
          if (isShooting && imgIndex == i) {
            // shooting
            fill = 'rgba(255, 0, 0, 0.5)'
          } else if (imgIndex >= 0 && imgIndex >= i) {
            // done
            fill = 'rgba(0, 255, 0, 0.25)';
          } else {
            // to be done
            fill = 'rgba(0, 0, 255, 0.25)'
          }


          this.drawEllipse(ctx, x, y, camX, camY, fill, i)

          if (x - (camX / 2) < 0) {
            this.drawEllipse(ctx, x + this.width, y, camX, camY, fill, i)
          }

          ++i;
        }
      }
    }
  }

  private drawPanoFov(ctx: CanvasRenderingContext2D) {
    if (this.panoFov_ && this.panoFov_.x && this.panoFov_.y) {

      ctx.beginPath();
      ctx.strokeStyle = 'black';
      if (!this.panoFov_.fullX && !this.panoFov_.fullY) {
        // rect
        let x1 = this.normalizeAndConvertX(this.panoFov_.x.from);
        let x2 = this.normalizeAndConvertX(this.panoFov_.x.to);
        let y1 = this.normalizeAndConvertY(this.panoFov_.y.from);
        let y2 = this.normalizeAndConvertY(this.panoFov_.y.to);
        //console.log('A1',{x1:this.panoFov_.x.from,x2:this.panoFov_.x.to}, {x1,x2})
        // TODO same with Y
        if(x1<0){
          x1 += this.width
          ctx.strokeRect(x1, y1, this.width, y2 - y1);
          ctx.strokeRect(x2, y1, -this.width, y2 - y1);
        }
        if(x2>this.width){
          x2 -= this.width
          ctx.strokeRect(x1, y1, this.width, y2 - y1);
          ctx.strokeRect(x2, y1, -this.width, y2 - y1);
        }

      } else if (this.panoFov_.fullX && !this.panoFov_.fullY) {
        // h-lines
        const y1 = this.normalizeAndConvertY(this.panoFov_.y.from);
        const y2 = this.normalizeAndConvertY(this.panoFov_.y.to);
        ctx.strokeRect(-1, y1, this.width + 2, y2 - y1);

      } else if (!this.panoFov_.fullX && this.panoFov_.fullY) {
        let x1 = this.normalizeAndConvertX(this.panoFov_.x.from);
        let x2 = this.normalizeAndConvertX(this.panoFov_.x.to);
        console.log('C1',{x1:this.panoFov_.x.from,x2:this.panoFov_.x.to}, {x1,x2})
        if(x1<0){
          x1 += this.width
        console.log('C2',{x1:this.panoFov_.x.from,x2:this.panoFov_.x.to}, {x1,x2})
          ctx.strokeRect(x1, -1, this.width, this.height + 2);
          ctx.strokeRect(x2, -1, -this.width, this.height + 2);
        }
        if(x2>this.width){
          x2 -= this.width
        console.log('C3',{x1:this.panoFov_.x.from,x2:this.panoFov_.x.to}, {x1,x2})
          ctx.strokeRect(x1, -1, this.width, this.height + 2);
          ctx.strokeRect(x2, -1, -this.width, this.height + 2);
        }

        // v-lines
        ctx.strokeRect(x1, -1, x2 - x1, this.height + 2);
      }else{
        // ignore, we don't need to draw a rectangle
      }
    }
  }

  private drawZeroCross(ctx: CanvasRenderingContext2D) {
    ctx.beginPath();
    ctx.strokeStyle = 'red';
    ctx.moveTo(0, this.height / 2); // Move the pen to (30, 50)
    ctx.lineTo(this.width, this.height / 2); // Draw a line to (150, 100)
    ctx.moveTo(this.width / 2, 0); // Move the pen to (30, 50)
    ctx.lineTo(this.width / 2, this.height); // Draw a line to (150, 100)
    ctx.stroke();
  }
}

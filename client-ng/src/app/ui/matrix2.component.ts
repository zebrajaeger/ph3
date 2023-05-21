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
import {FieldOfView, FieldOfViewPartial, PanoMatrix} from "../../data/pano";
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

  public pictureFov_!: FieldOfView;
  private pictureFovSubscription!: Subscription;

  public panoFov_!: FieldOfViewPartial;
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

    this.panoFovSubscription = this.panoService.requestPanoMatrix(matrix => this.panoMatrix = matrix);
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

  set pictureFov(fov: FieldOfView) {
    this.pictureFov_ = fov;
    this.draw();
  }

  set panoFov(fov: FieldOfViewPartial) {
    this.panoFov_ = fov;
    this.draw();
  }

  set panoMatrix(matrix: PanoMatrix) {
    this.panoMatrix_ = matrix;
    this.draw();
  }

  set robotState(value: RobotState) {
    this.draw();
    this._robotState = value;
  }

  private normalizeAndConvertX(deg: number): number {
    deg %= 360;
    if (deg < 0) {
      deg += 360;
    }
    return deg * this.width / 360;
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

    ctx.fillStyle ='black'
    ctx.fillText(`${i}`, x  - textW/2, y + textH/2);
  }

  private draw() {
    const ctx = this.canvas.nativeElement.getContext("2d");
    if (ctx === null) {
      return;
    }

    ctx.clearRect(0, 0, this.width, this.height);
    this.drawPanoFov(ctx);
    this.drawPictures(ctx);
    ctx.beginPath();
  }

  private drawPictures(ctx: CanvasRenderingContext2D) {
    if (this.panoMatrix_) {
      let px;
      let py;
      if (this.pictureFov_ && this.pictureFov_.horizontal && this.pictureFov_.vertical) {
        let ax = Math.abs(this.pictureFov_.horizontal.to - this.pictureFov_.horizontal.from);
        let ay = Math.abs(this.pictureFov_.vertical.to - this.pictureFov_.vertical.from);
        px = this.normalizeAndConvertX(ax);
        py = ay * this.height / 180;
      } else {
        px = 20;
        py = 20;
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
          let xx = row[xi];
          while (xx < 0) {
            xx += 360;
          }
          while (xx > 360) {
            xx -= 360;
          }
          const x = this.normalizeAndConvertX(xx);

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


          this.drawEllipse(ctx, x, y, px, py, fill, i)

          if (x - (px / 2) < 0) {
            this.drawEllipse(ctx, x + this.width, y, px, py, fill, i)
          }

          ++i;
        }
      }
    }
  }

  private drawPanoFov(ctx: CanvasRenderingContext2D) {
    if (this.panoFov_ && this.panoFov_.horizontal && this.panoFov_.vertical) {

      ctx.fillStyle = 'black';
      if (!this.panoFov_.fullX && !this.panoFov_.fullY) {
        // rect
        const x1 = this.normalizeAndConvertX(this.panoFov_.horizontal.from);
        const x2 = this.normalizeAndConvertX(this.panoFov_.horizontal.to);
        const y1 = this.normalizeAndConvertY(this.panoFov_.vertical.from);
        const y2 = this.normalizeAndConvertY(this.panoFov_.vertical.to);
        ctx.strokeRect(x1, y1, x2 - x1, y2 - y1);

      } else if (this.panoFov_.fullX && !this.panoFov_.fullY) {
        // h-lines
        const y1 = this.normalizeAndConvertY(this.panoFov_.vertical.from);
        const y2 = this.normalizeAndConvertY(this.panoFov_.vertical.to);
        ctx.strokeRect(-1, y1, this.width + 2, y2 - y1);

      } else if (!this.panoFov_.fullX && this.panoFov_.fullY) {
        const x1 = this.normalizeAndConvertX(this.panoFov_.horizontal.from);
        const x2 = this.normalizeAndConvertX(this.panoFov_.horizontal.to);
        // v-lines
        ctx.strokeRect(x1, -1, x2 - x1, this.height + 2);
      }
    }
  }
}

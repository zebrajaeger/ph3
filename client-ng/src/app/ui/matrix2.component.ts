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
import {RobotState} from "../../data/record";
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

  private drawEllipse(ctx: CanvasRenderingContext2D, x: number, y: number, w: number, h: number, fillStyle: string) {

    ctx.beginPath();
    ctx.fillStyle = fillStyle;
    ctx.strokeStyle = 'black';
    ctx.lineWidth = 1;
    ctx.ellipse(x, y, w / 2, h / 2, 0, 0, 360);
    ctx.fill();
    ctx.stroke();
  }

  private draw() {
    const ctx = this.canvas.nativeElement.getContext("2d");
    if (ctx === null) {
      return;
    }

    ctx.clearRect(0, 0, this.width, this.height);

    console.log('matrix2', 'draw', this.width, this.height);

    // draw pano bounds
    if (this.panoFov_ && this.panoFov_.horizontal && this.panoFov_.vertical) {
      console.log('matrix2', 'draw', 'panoFov', this.panoFov_);

      ctx.fillStyle = 'black';
      if (!this.panoFov_.fullX && !this.panoFov_.fullY) {
        // rect
        const x1 = this.normalizeAndConvertX(this.panoFov_.horizontal.from);
        const x2 = this.normalizeAndConvertX(this.panoFov_.horizontal.to);
        const y1 = this.normalizeAndConvertY(this.panoFov_.vertical.from);
        const y2 = this.normalizeAndConvertY(this.panoFov_.vertical.to);
        ctx.strokeRect(x1, y1, x2-x1, y2-y1);

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

    // half picture size
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

    // draw pictures
    if (this.panoMatrix_) {

      // const shotPos = cmd?.shotPosition;
      // this.x = shotPos?.xLength;
      // this.y = shotPos?.yLength;
      // if (shotPos && shotPos.index >= 0) {
      //   this.done = shotPos.index + 1;
      // }


      let i = 0;
      ctx.lineWidth = 2;
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

          // const fill = (xi + yi) % 2 == 0
          //     ? 'rgba(0, 0, 255, 0.25)'
          //     : 'rgba(0, 255, 0, 0.25)'
          this.drawEllipse(ctx, x, y, px, py, 'rgba(0, 0, 255, 0.25)')

          if (x - (px / 2) < 0) {
            this.drawEllipse(ctx, x + this.width, y, px, py, 'rgba(0, 0, 255, 0.25)')
          }

          ++i;
        }
      }
    }

    ctx.beginPath();
  }
}

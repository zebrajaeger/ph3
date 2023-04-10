import {
  AfterViewInit,
  Component,
  ElementRef,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges,
  ViewChild
} from '@angular/core';

@Component({
  selector: 'matrix',
  templateUrl: './matrix.component.html',
  styleUrls: ['./matrix.component.scss']
})
export class MatrixComponent implements OnInit, AfterViewInit, OnChanges {
  @Input()
  public width!: number;

  @Input()
  public height!: number;
  private _x?: number;
  private _y?: number;

  @Input()
  public done!: number;

  @Input()
  public set x(x) {
    this._x = x;
  }

  public get x() {
    return this._x;
  }

  @Input()
  public set y(y) {
    this._y = y;
  }

  public get y() {
    return this._y;
  }

  @Input()
  public color?: string;


  @ViewChild('matrix', {static: true})
  canvas!: ElementRef<HTMLCanvasElement>;

  constructor() {
  }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    this.draw();
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.draw();
  }

  private draw() {
    const canvas = this.canvas.nativeElement;
    const ctx = this.canvas.nativeElement.getContext("2d");
    if (ctx === null) {
      return;
    }

    const xn = this.x;
    // const xn = Math.random()*10;
    const yn = this.y;
    const w = canvas.width;
    const h = canvas.height;
    const v = this.done;

    ctx.clearRect(0, 0, w, h);

    if (!xn || !yn) {
      return;
    }
    // ctx.font = "20px serif";
    // ctx.fillText(`${xn}, ${yn}`, 10, 50);

    ctx.beginPath();
    for (let xi = 0; xi <= xn; ++xi) {
      const xl = (w / xn);
      const x = xl * xi
      for (let yi = 0; yi <= yn; ++yi) {
        const yl = (h / yn)
        const y = yl * yi

        const n = yi * xn + xi;
        if (n == v - 1 && this.color) {
          ctx.fillStyle = this.color;
        }
        if (n < v) {
          ctx.fillStyle = 'blue';
        } else {
          ctx.fillStyle = 'gray';
        }
        ctx.fillRect(x, y, xl, yl);
      }
    }

    // senkrecht
    for (let xi = 0; xi <= xn; ++xi) {
      const x = (w / xn) * xi
      ctx.moveTo(x, 0);
      ctx.lineTo(x, h);
    }
    //
    //waagerecht
    for (let yi = 0; yi <= yn; ++yi) {
      const y = (h / yn) * yi
      ctx.moveTo(0, y);
      ctx.lineTo(w, y);
    }
    ctx.lineWidth = 1;
    ctx.strokeStyle = 'white';
    ctx.stroke();
    ctx.closePath()
  }
}

import {
  AfterViewInit,
  Component,
  ElementRef,
  Input,
  NgZone,
  OnChanges,
  OnInit,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import {StatusBarTitle} from '../titlebar/titlebar.component';

@Component({
  selector: 'app-pano-status-grid',
  templateUrl: './pano-status-grid.component.html',
  styleUrls: ['./pano-status-grid.component.scss']
})
export class PanoStatusGridComponent implements AfterViewInit, OnChanges {

  @ViewChild('canvasId') canvasRef?: ElementRef;

  @Input()
  gridX = 10;
  @Input()
  gridY = 10;
  @Input()
  posX = 5;
  @Input()
  posY = 3;

  @Input()
  width = 100;
  @Input()
  height = 100;

  viewInitialized = false;

  constructor(private ngZone: NgZone) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!this.viewInitialized) {
      return;
    }
    this.draw();
  }

  draw(): void {
    const ctx: CanvasRenderingContext2D = this.canvasRef?.nativeElement.getContext('2d');
    if (ctx === undefined) {
      return;
    }

    const sizeWidth = ctx.canvas.clientWidth;
    const sizeHeight = ctx.canvas.clientHeight;
    ctx.clearRect(0, 0, sizeWidth, sizeHeight);

    // console.log('xxx', {sizeWidth, sizeHeight});
    ctx.beginPath();
    for (let x = 0; x <= this.gridX; ++x) {
      const xPos = sizeWidth * x / this.gridX;
      ctx.moveTo(xPos, 0);
      ctx.lineTo(xPos, sizeHeight);
    }
    for (let y = 0; y <= this.gridY; ++y) {
      const yPos = sizeHeight * y / this.gridY;
      ctx.moveTo(0, yPos);
      ctx.lineTo(sizeWidth, yPos);
    }
    ctx.lineWidth = 1.5;
    ctx.strokeStyle = '#000';
    ctx.stroke();

    ctx.beginPath();
    for (let y = 0; y < this.gridY; ++y) {
      for (let x = 0; x < this.gridX; ++x) {
        if ((y > this.posY) || (y >= this.posY && x >= this.posX)) {
          continue;
        }

        const xPos1 = sizeWidth * x / this.gridX;
        const xPos2 = sizeWidth * (x + 1) / this.gridX;
        const yPos1 = sizeHeight * y / this.gridY;
        const yPos2 = sizeHeight * (y + 1) / this.gridY;
        ctx.moveTo(xPos1 + 2, yPos1 + 2);
        ctx.lineTo(xPos2 - 2, yPos1 + 2);
        ctx.lineTo(xPos2 - 2, yPos2 - 2);
        ctx.lineTo(xPos1 + 2, yPos2 - 2);
      }
    }
    ctx.fillStyle = '#AAF';
    ctx.fill();
  }

  ngAfterViewInit(): void {
    this.viewInitialized = true;
    this.draw();
  }

  resize(): void {
    console.log('RESIZE');
  }
}

import { Component, OnInit, ElementRef, ViewChild, AfterViewInit, Input, Output, EventEmitter, HostListener } from '@angular/core';
import { Position } from 'src/data/panohead';

@Component({
  selector: 'app-touchpad',
  templateUrl: './touchpad.component.html',
  styleUrls: ['./touchpad.component.scss']
})
export class TouchpadComponent implements OnInit, AfterViewInit {
  @Input() print = true;
  @Input() baseMultiplier = 0.005;
  @Input() multiplyer = this.baseMultiplier * 5;
  @Input() doubleTabMaxTime = 300;
  @Output() relPosition = new EventEmitter<Position>();
  @Output() doubleTap = new EventEmitter<void>();

  @ViewChild('touchpadCanvas', { static: true })
  canvas!: ElementRef<HTMLCanvasElement>;
  private ctx!: CanvasRenderingContext2D;

  private isDrawing = false;
  private lastX = 0;
  private lastY = 0;
  private lastTime = 0;
  private touchStartTime = 0;
  private lastTouchEndTime = 0;

  constructor() { }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    this.initializeCanvas();
  }

  private initializeCanvas(): void {
    console.log('init', this.canvas)
    if (!this.canvas) return;

    this.ctx = this.canvas.nativeElement.getContext('2d')!;
    this.ctx.strokeStyle = '#000';
    this.ctx.lineJoin = 'round';
    this.ctx.lineCap = 'round';
    this.ctx.lineWidth = 5;

    this.canvas.nativeElement.addEventListener('mousedown', this.handleMouseDown.bind(this));
    this.canvas.nativeElement.addEventListener('touchstart', this.handleTouchStart.bind(this));

    this.canvas.nativeElement.addEventListener('mousemove', this.handleMouseMove.bind(this));
    this.canvas.nativeElement.addEventListener('touchmove', this.handleTouchMove.bind(this));

    this.canvas.nativeElement.addEventListener('mouseup', this.handleMouseUp.bind(this));
    this.canvas.nativeElement.addEventListener('mouseout', this.handleMouseUp.bind(this));
    this.canvas.nativeElement.addEventListener('touchend', this.handleTouchEnd.bind(this));
    this.canvas.nativeElement.addEventListener('touchcancel', this.stopDrawing.bind(this));

    this.resizeCanvas();
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: Event): void {
    this.resizeCanvas();
  }

  private resizeCanvas() {
    const rect = this.canvas.nativeElement.getBoundingClientRect();
    this.canvas.nativeElement.width = rect.width;
    this.canvas.nativeElement.height = rect.height;
  }

  private handleTouchStart(event: TouchEvent): void {
    this.startDrawing(event);
  }
  private handleTouchEnd(event: TouchEvent): void {
    this.stopDrawing();
  }
  private handleTouchMove(event: TouchEvent): void {
    this.draw(event);
  }

  private handleMouseDown(event: MouseEvent): void {
    this.startDrawing(event);
  }
  private handleMouseUp(event: MouseEvent): void {
    this.stopDrawing();
  }
  private handleMouseMove(event: MouseEvent): void {
    this.draw(event);
  }


  private startDrawing(event: MouseEvent | TouchEvent): void {
    this.isDrawing = true;
    [this.lastX, this.lastY] = this.getCoordinates(event);
    this.lastTime = Date.now();

    const currentTime = new Date().getTime();
    const timeSinceLastTouch = currentTime - this.lastTouchEndTime;

    if (timeSinceLastTouch < this.doubleTabMaxTime) {
      this.doubleTap.emit(); // Doppeltippen erkannt, Event auslösen
    }

    this.touchStartTime = currentTime;
  }

  private stopDrawing(): void {
    this.isDrawing = false;
    const currentTime = new Date().getTime();
    const touchDuration = currentTime - this.touchStartTime;

    if (touchDuration < this.doubleTabMaxTime) {
      this.lastTouchEndTime = currentTime;
    }

  }

  private draw(event: MouseEvent | TouchEvent): void {
    if (!this.isDrawing) return;
    event.preventDefault();

    const [x, y] = this.getCoordinates(event);
    const currentTime = Date.now();
    const timeDiff = currentTime - this.lastTime;

    const distance = Math.sqrt(Math.pow(x - this.lastX, 2) + Math.pow(y - this.lastY, 2));
    const speed = distance / timeDiff;

    let multiplier = this.baseMultiplier;
    // Farbe basierend auf der Anzahl der Finger setzen
    if (event instanceof MouseEvent) {
      this.ctx.strokeStyle = '#FF0000'; // Rot für Maus
    } else {
      const touches = event.touches.length;
      switch (touches) {
        case 1:
          this.ctx.strokeStyle = '#FF0000'; // Rot für einen Finger
          break;
        case 2:
          this.ctx.strokeStyle = '#00FF00'; // Grün für zwei Finger
          multiplier = this.multiplyer;
          break;
        default:
          this.ctx.strokeStyle = '#000'; // Standardfarbe
      }
    }

    // Strichstärke basierend auf der Geschwindigkeit setzen
    const stroke = Math.max(1, Math.min(10, speed * speed));
    console.log({ speed });
    const moveX = x - this.lastX;
    const moveY = y - this.lastY;
    const xx = moveX * stroke * multiplier;
    const yy = moveY * stroke * multiplier

    if (this.print) {
      this.ctx.lineWidth = stroke;
      this.ctx.beginPath();
      this.ctx.moveTo(this.lastX, this.lastY);
      this.ctx.lineTo(x, y);
      this.ctx.stroke();
    }

    this.relPosition.emit(new Position(xx, yy));

    [this.lastX, this.lastY] = [x, y];
    this.lastTime = currentTime;
  }

  private getCoordinates(event: MouseEvent | TouchEvent): [number, number] {
    if (event instanceof MouseEvent) {
      return [event.offsetX, event.offsetY];
    } else {
      const touch = event.touches[0];
      const rect = this.canvas.nativeElement.getBoundingClientRect();
      return [touch.clientX - rect.left, touch.clientY - rect.top];
    }
  }
}

export class Range {
  from!: number;
  to!: number;
  size?: number;
  complete?: boolean;
}

export class FieldOfView {
  horizontal!: Range;
  vertical!: Range;
}

export class FieldOfViewPartial extends FieldOfView {
  fullX!: boolean;
  fullY!: boolean;
}

export enum Border {
  TOP = 'TOP',
  RIGHT = 'RIGHT',
  BOTTOM = 'BOTTOM',
  LEFT = 'LEFT'
}

//{"maxY":31.245065789473685,"maxXSize":6,"maxX":300.0,"ypositions":[8.748618421052635,31.245065789473685],"minY":8.748618421052635,"minX":0.0,"xpositions":[[0.0,60.0,120.0,180.0,240.0,300.0],[0.0,60.0,120.0,180.0,240.0,300.0]],"ysize":2}
export class PanoMatrix {
  yPositions!: number[];
  xPositions!: number[][];

  minX!: number;
  maxX!: number;
  maxXSize!: number
  minY!: number;
  maxY!: number;
  ySize!: number;

  positionCount!: number
}

export class Delay {
  waitAfterMove!: number;
  waitAfterShot!: number;
  waitBetweenShots!: number;
}

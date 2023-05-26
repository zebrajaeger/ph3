export class Range {
    from!: number;
    to!: number;

    constructor(jsonObject: Range) {
        Object.assign(this, jsonObject);
    }

    public get size() {
        return this.complete ? Math.abs(this.to - this.from) : undefined;
    }

    public get complete() {
        return this.from != undefined && this.to != undefined;
    }
}

export class CameraOfView {
    x!: Range;
    y!: Range;

    constructor(jsonObject: CameraOfView) {
        this.x = new Range(jsonObject.x);
        this.y = new Range(jsonObject.y);
    }
}

export class PanoFieldOfView {
    x!: Range;
    y!: Range;
    fullX!: boolean;
    fullY!: boolean;

    constructor(jsonObject: PanoFieldOfView) {
        this.x = new Range(jsonObject.x);
        this.y = new Range(jsonObject.y);
        this.fullX = jsonObject.fullX;
        this.fullY = jsonObject.fullY;
    }
}

export enum Border {
    TOP = 'TOP',
    RIGHT = 'RIGHT',
    BOTTOM = 'BOTTOM',
    LEFT = 'LEFT'
}

export enum Pattern {
    GRID = <any>"GRID",
    SPARSE = <any>"SPARSE"
}

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

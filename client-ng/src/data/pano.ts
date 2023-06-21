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

    public normalizeDirection() {
        if (this.complete && this.from > this.to) {
            const temp = this.from;
            this.from = this.to;
            this.to = temp;
        }
    }
}

export class CameraOfView {
    x!: Range;
    y!: Range;

    constructor(jsonObject: CameraOfView) {
        this.x = new Range(jsonObject.x);
        this.y = new Range(jsonObject.y);
    }

    public normalizeDirection() {
        this.x.normalizeDirection();
        this.y.normalizeDirection();
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

    normalizeDirection() {
        this.x.normalizeDirection();
        this.y.normalizeDirection();
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

export class PanoMatrixPosition {
    id!: number;
    x!: number;
    y!: number;
}
export class PanoMatrix {
    yPositions!: number[];
    xPositions!: PanoMatrixPosition[][];

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

export class GpsFlags {
    public altitude!: boolean;
    public date!: boolean;
    public hdop!: boolean;
    public location!: boolean;
    public satellites!: boolean;
    public time!: boolean;
}

export class GeoLocation {
    public latitude!: number;
    public longitude!: number;
    public altitude!: number;
}

export class GpsMetaData {
    public satellites!: number;
    public hdop!: number;
}

export class Gps {
    public geoLocation!: GeoLocation;
    public gpsMetaData!: GpsMetaData;
    public updated!: GpsFlags;
    public valid!: GpsFlags;
}

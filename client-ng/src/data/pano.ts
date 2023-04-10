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
    partial!: boolean;
}

export enum Border {
    TOP = 'TOP',
    RIGHT = 'RIGHT',
    BOTTOM = 'BOTTOM',
    LEFT = 'LEFT'
}

export class CalculatedPano {
    horizontalPositions!: number[];
    verticalPositions!: number[];

    horizontalOverlap!: number;
    verticalOverlap!: number;
}

export class Delay {
    waitAfterMove!: number;
    waitAfterShot!: number;
    waitBetweenShots!: number;
}

export class ActorAxis {
    public pos!: number;
    public speed!: number;
    public isMoving!: boolean;
    public atTargetPos!: boolean;
}

export class ActorState {
    public x!: ActorAxis;
    public y!: ActorAxis;
}

export class Power {
    public voltage: number;
    public current: number;
    public power: number;

    constructor(body: string) {
        const o = JSON.parse(body);
        this.voltage = o.voltage;
        this.current = o.current;
        this.power = o.power;
    }

    public toString(): string {
        return `U:${this.voltage?.toFixed(1)}V I:${this.current?.toFixed(1)}A P:${this.power?.toFixed(1)}W`;
    }
}

export class BatteryState {
    valid!: boolean;
    percentage!: number;
}

export class Position {
    constructor(x: number, y: number) {
        this.x = x;
        this.y = y;
    }
    public x!: number;
    public y!: number;
}

export class AxesPosition {
    public targetRawPosition!: Position;
    public targetDegPosition!: Position;
    public measuredRawPosition!: Position;
    public measuredDegPosition!: Position;
}

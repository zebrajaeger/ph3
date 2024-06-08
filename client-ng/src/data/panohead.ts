import {Camera} from './camera';

export class ActorAxis {
    public pos!: number;
    public speed!: number;
    public isMoving!: boolean;
    public atTargetPos!: boolean;
}

export class Actor {
    public x!: ActorAxis;
    public y!: ActorAxis;
}

export class ActorState {
    public x!: number;
    public y!: number;
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
    public x!: number;
    public y!: number;
}

export class PanoHeadData {
    public actor!: Actor;
    public camera!: Camera;
    public movementRaw!: number;
    public cameraRaw!: number;
}

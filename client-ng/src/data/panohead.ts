import {Camera} from './camera';

export class ActorAxis {
    public pos: number;
    public speed: number;
    public isMoving: boolean;
    public atTargetPos: boolean;
}

export class Actor {
    public x: ActorAxis;
    public y: ActorAxis;
}


export class PanoHeadData {
    public actor: Actor;
    public camera: Camera;
    public movementRaw: number;
    public cameraRaw: number;
}

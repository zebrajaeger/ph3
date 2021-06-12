import {Shot} from './camera';

export enum AutomateState {
    STOPPED, STOPPED_WITH_ERROR, STARTED, CMD_DELAY, CMD_SHOT, CMD_MOVE, STOPPING
}

export enum PauseState {
    RUNNING, PAUSE_REQUESTED, PAUSING
}

export class Position {
    x: number;
    y: number;
}

export class Command {
    description: string;
    timeMs?: number;
    position?: Position;
    shot?: Shot;
}

export class RecordState {
    automateState: AutomateState;
    pauseState: PauseState;
    command: Command;
    commandIndex: number;
    commandCount: number;
}

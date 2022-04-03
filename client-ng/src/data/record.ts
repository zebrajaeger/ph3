import {Shot} from './camera';

export enum AutomateState {
    STOPPED = 'STOPPED',
    STOPPED_WITH_ERROR = 'STOPPED_WITH_ERROR',
    STARTED = 'STARTED',
    CMD_DELAY = 'CMD_DELAY',
    CMD_SHOT = 'CMD_SHOT',
    CMD_MOVE = 'CMD_MOVE',
    STOPPING = 'STOPPING'
}

export enum PauseState {
    RUNNING = 'RUNNING',
    PAUSE_REQUESTED = 'PAUSE_REQUESTED',
    PAUSING = 'PAUSING'
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

    constructor(body: string) {
        const o = JSON.parse(body);
        this.automateState = AutomateState[o.automateState];
        this.pauseState = PauseState[o.pauseState];
        this.command = o.command;
        this.commandIndex = o.commandIndex;
        this.commandCount = o.commandCount;
    }
}

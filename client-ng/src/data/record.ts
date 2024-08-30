import {Shot} from './camera';

export enum AutomateState {
  STOPPING = 'STOPPING',
  STOPPED = 'STOPPED',
  STOPPED_WITH_ERROR = 'STOPPED_WITH_ERROR',

  STARTED = 'STARTED',

  EXEC_DELAY = 'EXEC_DELAY',
  EXEC_SHOT = 'EXEC_SHOT',
  EXEC_MOVE = 'EXEC_MOVE',

  APPLY_OFFSET = 'APPLY_OFFSET',
  NORMALIZE_POSITION = 'NORMALIZE_POSITION'
}

export enum PauseState {
  RUNNING = 'RUNNING',
  PAUSE_REQUESTED = 'PAUSE_REQUESTED',
  PAUSING = 'PAUSING'
}

export class Position {
  x!: number;
  y!: number;
}

export class ShotPosition {
  x!: number;
  y!: number;
  index!: number;
  xIndex!: number;
  xLength!: number;
  yIndex!: number;
  yLength!: number;
}

export class Command {
  shotPosition!: ShotPosition;
  description!: string;
  timeMs?: number;
  shot?: Shot;
}

// export class RobotState {
//   automateState!: AutomateState;
//   pauseState!: PauseState;
//   command?: Command;
//   commandIndex!: number;
//   commandCount!: number;

//   constructor(body: string) {
//     const o = JSON.parse(body);
//     this.automateState = (<any>AutomateState)[o.automateState];
//     this.pauseState = (<any>PauseState)[o.pauseState];
//     this.command = o.command;
//     this.commandIndex = o.commandIndex;
//     this.commandCount = o.commandCount;
//   }
// }

export class RecordState {
  states!: string[];
  command?: Command;
  commandIndex!: number;
  commandCount!: number;

  constructor(body: string) {
    // console.log(body)
    const o = JSON.parse(body);
    this.states = o.states;
    this.command = o.command;
    this.commandIndex = o.commandIndex;
    this.commandCount = o.commandCount;
  }
}

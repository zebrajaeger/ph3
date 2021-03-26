const {Subject} = require('rxjs')

//<editor-fold desc="Commands">
class Command {
    _description;
    _index;

    constructor(index, description) {
        this._index = index;
        this._description = description;
    }

    get name() {
        return this.constructor.name;
    }

    get index() {
        return this._index;
    }

    get description() {
        return this._description;
    }
}

class GoToPosCommand extends Command {
    _pos;

    constructor(index, description, pos) {
        super(index, description)
        this._pos = pos;
    }

    get pos() {
        return this._pos;
    }
}

class TakeShotCommand extends Command {
    _focusTime;
    _triggerTime;

    constructor(index, description, focusTime, triggerTime) {
        super(index, description)
        this._focusTime = focusTime;
        this._triggerTime = triggerTime;
    }

    get focusTime() {
        return this._focusTime;
    }

    get triggerTime() {
        return this._triggerTime;
    }
}

class WaitCommand extends Command {
    _timeMs;

    constructor(index, description, timeMs) {
        super(index, description)
        this._timeMs = timeMs;
        this._index = index;
    }


    get timeMs() {
        return this._timeMs;
    }
}

//</editor-fold>

class TakeShotSettings {
    focusTime = 1000;
    shotTime = 1000;
}

class TakePanoSettings {
    waitAfterMove = 0;
    waitAfterShot = 0;
    waitBetweenShots = 0;
    takeShotSettings = []
}

class CommandListGenerator {
    _commands = [];

    createCommands(pano, takePanoSettings) {
        const commands = [];
        let index = 0;

        // columns
        for (let iy = 0; iy < pano.y.n; ++iy) {
            const y = pano.y.startPositions[iy];

            // rows
            for (let ix = 0; ix < pano.x.n; ++ix) {
                const x = pano.x.startPositions[ix];

                const pos = {x, y}
                this.commands.push(new GoToPosCommand(index++, 'GoTo', pos))
                this.commands.push(new WaitCommand(index++, 'WaitAfterMove', takePanoSettings.waitAfterMove))

                // shots
                for (let s = 0; s < takePanoSettings.takeShotSettings.length; ++s) {
                    this.commands.push(new TakeShotCommand(index++, 'Shot', takePanoSettings.focusTime, takePanoSettings.shotTime))

                    const isLast = ((s + 1) < takePanoSettings.takeShotSettings.length);
                    if (isLast) {
                        this.commands.push(new WaitCommand(index++, 'WaitBetweenShots', takePanoSettings.waitBetweenShots))
                    }
                }

                this.commands.push(new WaitCommand(index++, 'WaitAfterShot', takePanoSettings.waitAfterShot))
            }
        }
        return commands;
    }
}

class Automat {
    _commands;
    _commandIndex;

    _stopRequest = false;
    _pauseRequest = false;
    _isRunning = false;

    _startListeners = new Subject();
    _commandListeners = new Subject();
    _goToPosListeners = new Subject();
    _takeShotListeners = new Subject();
    _waitListeners = new Subject();
    _finishListeners = new Subject();

    constructor() {
        this.#reset();
    }

    #reset() {
        this._commandIndex = undefined;
        this._stopRequest = false;
        this._pauseRequest = false;
        this._isRunning = false;
    }

    #execNextCommand() {
        if (!this.isRunning) {
            return;
        }

        if (this.stopRequest) {
            this.#notifyFinish()
            this.#reset();
        }

        if (this.pauseRequest) {
            this._isRunning = false;
            return;
        }

        if (this.commandIndex !== undefined) {
            // execute first command after start
            this._commandIndex = 0;
            this.#notifyStart();
        } else {
            this._commandIndex++;
            if (this.commands.length > this.commandIndex) {
                // yes, has another command -> execute
                const command = this._commands[this._commandIndex];
                setInterval(() => {
                    this.#execCommand(command);
                }, 0)
            } else {
                // not further command available -> stop
                this._commandIndex = undefined;
                this.#notifyFinish()
                this.#reset()
            }
        }
    }

    #execCommand(command) {
        this.#notifyCommand(command);

        if (command instanceof GoToPosCommand) {
            this.#notifyGoToPos(command.pos);

        } else if (command instanceof TakeShotCommand) {
            this.#notifyTakeShot(command.focusTime, command.shotTime)

        } else if (command instanceof WaitCommand) {
            setInterval(() => {
                this.onWaitDone()
            }, command.timeMs)
        } else {
            throw Error(`Unknown command: "${command}"`)
        }
    }

    start(commandList) {
        if (!this.isRunning) {
            this.#reset();
            this._commands = commandList;
            this._isRunning = true;
        } else {
            throw Error('Already running')
        }
    }

    stop() {
        if (this.isRunning) {
            this._stopRequest = true;
        }
    }

    pauseResume() {
        if (!this.stopRequest) {
            return;
        }

        if (this.isRunning) {
            this._pauseRequest = !this._pauseRequest;
        } else {
            // stopped. we need to restart.maybe.
            this._pauseRequest = !this._pauseRequest
            if (this.pauseRequest) {
                this.#execNextCommand();
            }
        }
    }

    onPosReached() {
        this.ifCommandAvailable(GoToPosCommand, (command) => this.#execNextCommand());
    }

    onShotDone() {
        this.ifCommandAvailable(TakeShotCommand, (command) => this.#execNextCommand());
    }

    onWaitDone() {
        this.ifCommandAvailable(WaitCommand, (command) => this.#execNextCommand());
    }

    //<editor-fold desc="register to events">
    registerStartListener(callback) {
        this._startListeners.subscribe(callback)
    }

    registerCommandListener(callback) {
        this._commandListeners.subscribe(callback)
    }

    registerGoToPosListener(callback) {
        this._goToPosListeners.subscribe(callback)
    }

    registerTakeShotListener(callback) {
        this._takeShotListeners.subscribe(callback)
    }

    registerWaitListener(callback) {
        this._waitListeners.subscribe(callback)
    }

    //</editor-fold>

    //<editor-fold desc="notifications">
    #notifyStart() {
        this._startListeners.next();
    }

    #notifyCommand(command) {
        this._commandListeners.next(command);
    }

    #notifyGoToPos(pos) {
        this._goToPosListeners.next();
    }

    #notifyTakeShot(focusTime, shotTime) {
        this._takeShotListeners.next({focusTime, shotTime});
    }

    #notifyFinish() {
        this._finishListeners.next();
    }

    //</editor-fold>

    //<editor-fold desc="getter/setter">

    get commands() {
        return this._commands;
    }

    get command() {
        if (this.commandIndex !== undefined && this.commandIndex > this.commands.length) {
            return this.commands[this.commandIndex];
        }
        return undefined;
    }

    ifCommandAvailable(type, callback) {
        if (this.commandIndex !== undefined && this.commandIndex > this.commands.length) {
            const command = this.commands[this.commandIndex];
            if (command instanceof type) {
                callback()
            }
        }
    }

    get commandIndex() {
        return this._commandIndex;
    }


    get stopRequest() {
        return this._stopRequest;
    }

    get pauseRequest() {
        return this._pauseRequest;
    }

    get isRunning() {
        return this._isRunning;
    }

//</editor-fold>
}

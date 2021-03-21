const {Subject} = require('rxjs')

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
        for (let y = 0; y < pano.y.n; ++y) {

            // rows
            for (let x = 0; x < pano.x.n; ++x) {

                const pos = {x: 0, y: 0} // TODO
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
    _generator;
    _commands;
    _commandIndex;

    _stopRequest = false;

    _startListeners = new Subject();
    _commandListeners = new Subject();
    _goToPosListeners = new Subject();
    _takeShotListeners = new Subject();
    _waitListeners = new Subject();
    _finishListeners = new Subject();

    constructor() {
        this._generator = new CommandListGenerator();
        this._commands = [];
        this._commandIndex = undefined;
    }

    #execNextCommand() {
        if (this.commandIndex !== undefined) {
            this._commandIndex = 0;
            this.#notifyStart();
        } else {
            this.commandIndex++;
            if (this.commands.length > this.commandIndex) {
                const command = this._commands[this._commandIndex];
                setInterval(() => {
                    this.#execCommand(command);
                }, 0)
            } else {
                this._commandIndex = undefined;
                this.#notifyFinish()
            }
        }
    }

    #execCommand(command) {
        if ()

            this.#notifyCommand(command);

        if (command instanceof GoToPosCommand) {
            this.#notifyGoToPos(command.pos);

        } else if (command instanceof TakeShotCommand) {
            this.#notifyTakeShot(command.focusTime, command.shotTime)

        } else if (command instanceof WaitCommand) {
            setInterval(() => {
                this.#execNextCommand();
            }, command.timeMs)
        } else {
            throw Error(`Unknown command: "${command}"`)
        }
    }

    //<editor-fold desc="Outside triggers">
    start(commandList) {
        if (this._commandIndex === undefined) {
            this._commands = commandList;
            this._commandIndex = undefined;
        } else {
            throw Error('already running')
        }
    }

    stop() {
        if (this._commandIndex !== undefined) {
            this._stopRequest = true;
        }
    }

    onPosReached() {
        const command = this.command;
        if (command && command instanceof GoToPosCommand) {
            this.#execNextCommand();
        }
    }

    onShotDone() {
        const command = this.command;
        if (command && command instanceof TakeShotCommand) {
            this.#execNextCommand();
        }
    }

    //</editor-fold>

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
    get generator() {
        return this._generator;
    }

    get commands() {
        return this._commands;
    }

    get command() {
        if (this.commandIndex !== undefined && this.commandIndex > this.commands.length) {
            return this.commands[this.commandIndex];
        }
        return undefined;
    }

    get commandIndex() {
        return this._commandIndex;
    }

    //</editor-fold>
}

const EventEmitter = require('events');

const {ReadDataBuffer, WriteDataBuffer} = require('@zebrajaeger/remote-i2c');

class Controller extends EventEmitter {
    _i2cAddress;
    _intervalHandler;
    _data = null;

    constructor(i2c, i2cAddress, interval) {
        super();

        this._i2cAddress = i2cAddress;

        this._intervalHandler = setInterval(async () => {
            await i2c.read(this.i2cAddress, 14)
                .then(buffer => new ReadDataBuffer(buffer))
                .then(rdb => {
                    const data = {status: {}, x: {}, y: {}, camera: {}};

                    data.status.movement = rdb.readUInt8();
                    data.x.pos = rdb.readUInt32LE();
                    data.x.speed = rdb.readInt16LE();
                    data.y.pos = rdb.readUInt32LE();
                    data.y.speed = rdb.readInt16LE();
                    data.status.camera = rdb.readUInt8();

                    // set bit values for steppers
                    data.x.atTargetPos = ((data.status.movement & 0x01) !== 0);
                    data.x.isMoving = ((data.status.movement & 0x02) !== 0);
                    data.y.atTargetPos = ((data.status.movement & 0x04) !== 0);
                    data.y.isMoving = ((data.status.movement & 0x08) !== 0);

                    // set bit values for camera
                    data.camera.focus = ((data.status.camera & 0x01) !== 0);
                    data.camera.trigger = ((data.status.camera & 0x02) !== 0);

                    this._data = data;
                    this.emit('value', data)
                })
                .catch(err => this.emit('error', err));
        }, interval)
        this._i2c = i2c;
    }

    #send() {
        // stepperWriteLimit = 0x20,
        // stepperWriteVelocity = 0x21,
        // stepperWritePos = 0x22,
        //
        // cameraStartFocus = 0x30,
        // cameraStartTrigger = 0x31,
        // cameraStartShot = 0x32,
    }

    async startFocus(timeMs) {
        const wdb = new WriteDataBuffer();
        wdb.writeUInt8(0x30);
        wdb.writeUInt32LE(timeMs);
        this.i2c.write(this.i2cAddress, wdb.create());
    }

    async startTrigger(timeMs) {
        const wdb = new WriteDataBuffer();
        wdb.writeUInt8(0x31);
        wdb.writeUInt32LE(timeMs);
        this.i2c.write(this.i2cAddress, wdb.create());
    }

    async startShot(focusTimeMs, triggerTimeMs) {
        const wdb = new WriteDataBuffer();
        wdb.writeUInt8(0x32);
        wdb.writeUInt32LE(triggerTimeMs);
        wdb.writeUInt32LE(triggerTimeMs);
        this.i2c.write(this.i2cAddress, wdb.create());
    }

    async setLimit(axis, limit) {
        const wdb = new WriteDataBuffer();
        wdb.writeUInt8(0x20);
        wdb.writeUInt8(axis);
        wdb.writeUInt32LE(limit);
        this.i2c.write(this.i2cAddress, wdb.create());
    }

    async setTargetVelocity(axis, velocity) {
        const wdb = new WriteDataBuffer();
        wdb.writeUInt8(0x21);
        wdb.writeUInt8(axis);
        wdb.writeInt32LE(velocity);
        this.i2c.write(this.i2cAddress, wdb.create());
    }

    async setTargetPos(axis, pos) {
        const wdb = new WriteDataBuffer();
        wdb.writeUInt8(0x22);
        wdb.writeUInt8(axis);
        wdb.writeUInt32LE(pos);
        this.i2c.write(this.i2cAddress, wdb.create());
    }

    get intervalHandler() {
        return this._intervalHandler;
    }

    get i2cAddress() {
        return this._i2cAddress;
    }

    get i2c() {
        return this._i2c;
    }

    get data() {
        return this._data;
    }
}

module.exports = {Controller};

const EventEmitter = require('events');

const {ReadDataBuffer} = require('@zebrajaeger/remote-i2c');

const {map, isSet} = require('./utils');

class Axis {
    _threshold;
    _raw;
    _calculated;
    _autoCalibrationEnabled;

    constructor(autoCalibrationEnabled) {
        this._autoCalibrationEnabled = autoCalibrationEnabled || true;
        this._threshold = null;
        this.#reset();
    }

    setAsCenter() {
        this.raw.center = this.raw.value;
    }

    set rawValue(value) {
        this.raw.value = value;
        this.#calculate();
    }

    #reset() {
        this._raw = {value: null, min: null, center: null, max: null};
        this._calculated = {value: null, capped: null};
    }

    #calculate() {
        const raw = this.raw;
        const calculated = this.calculated;
        const threshold = this.threshold;

        if (this._autoCalibrationEnabled) {
            if (raw.min === null || raw.min > raw.value) {
                raw.min = raw.value;
            }
            if (raw.max === null || raw.max < raw.value) {
                raw.max = raw.value;
            }
        }

        // map raw to calculated.value
        let current;
        if (raw.center === null) {
            current = map(raw.value, raw.min, raw.max, 0, 1);
        } else {
            if (raw.value < raw.center) {
                current = map(raw.value, raw.min, raw.center, 0, 0.5);
            } else {
                current = map(raw.value, raw.center, raw.max, 0.5, 1);
            }
        }

        calculated.value = current;

        // map calculated.value to calculated.cappped
        if (current > 1) {
            calculated.capped = 1;
        } else if (current < 0) {
            calculated.capped = 0;
        } else {
            if (threshold === null) {
                calculated.capped = current;
            } else {
                const topCenter = 0.5 + threshold;
                const botCenter = 0.5 - threshold;
                if (current > topCenter) {
                    calculated.capped = map(current, topCenter, 1, 0.5, 1);
                } else if (current < botCenter) {
                    calculated.capped = map(current, 0, botCenter, 0, 0.5);
                } else {
                    calculated.capped = 0.5;
                }
            }
        }
    }

    resetCalibration() {
        this._raw = {value: this._raw.value, min: null, center: null, max: null};
        this._calculated = {value: null, capped: null};
        this.#calculate();
    }

    get autoCalibrationEnabled() {
        return this._autoCalibrationEnabled;
    }

    set autoCalibrationEnabled(value) {
        this._autoCalibrationEnabled = value;
    }

    set threshold(value) {
        this._threshold = value;
    }

    get threshold() {
        return this._threshold;
    }

    get raw() {
        return this._raw;
    }

    get calculated() {
        return this._calculated;
    }

    get values() {
        return {
            threshold: this.threshold,
            raw: Object.assign({}, this.raw),
            calculated: Object.assign({}, this.calculated)
        }
    }

    get calibration() {
        return {min: this.raw.min, center: this.raw.center, max: this.raw.max}
    }

    set calibration(value) {
        isSet(value.min, v => this.raw.min = v);
        isSet(value.center, v => this.raw.center = v);
        isSet(value.max, v => this.raw.max = v);
    }
}

class I2CJoystick extends EventEmitter {
    _i2cAddress;
    _intervalHandler;
    _x;
    _y;
    _autoCalibrationEnabled;

    constructor(i2c, i2cAddress, interval, autocalibrationEnabled) {
        super();
        this._i2cAddress = i2cAddress;
        this._autoCalibrationEnabled = autocalibrationEnabled;

        this._x = new Axis();
        this._y = new Axis();

        this._intervalHandler = setInterval(async () => {
            await i2c.read(this.i2cAddress, 4)
                .then(buffer => {
                    return new ReadDataBuffer(buffer)
                })
                .then(rdb => {
                    const x = rdb.readUInt16LE();
                    const y = rdb.readUInt16LE();
                    this.x.rawValue = x;
                    this.y.rawValue = y;
                    const data = {x: this.x.values, y: this.y.values}
                    this.emit('value', data)
                })
                .catch(err => this.emit('error', err));
        }, interval)
    }

    resetCalibration() {
        this.x.resetCalibration();
        this.y.resetCalibration();
    }

    setCurrentPositionAsCenter() {
        this.x.setAsCenter();
        this.y.setAsCenter();
    }

    set threshold(value) {
        this.x.threshold = value;
        this.y.threshold = value;
    }


    get autoCalibrationEnabled() {
        return this._autoCalibrationEnabled;
    }

    set autoCalibrationEnabled(value) {
        const enabled = (value === true);
        this.x.autoCalibrationEnabled = enabled;
        this.y.autoCalibrationEnabled = enabled;
        this._autoCalibrationEnabled = value;
    }

    get x() {
        return this._x;
    }

    get y() {
        return this._y;
    }

    get i2cAddress() {
        return this._i2cAddress;
    }

    get calibration() {
        return {x: this.x.calibration, y: this.y.calibration};
    }

    set calibration(value) {
        isSet(value.x, v => this.x.calibration = v);
        isSet(value.y, v => this.y.calibration = v);
    }
}

module.exports = {I2CJoystick, Axis}

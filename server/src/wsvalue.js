const EventEmitter = require('events');

function booleanFilter(value) {
    return value === true;
}

class WsValue extends EventEmitter {
    _currentValue;
    _names = {raw: null, capitalized: null, is: null, get: null, set: null};

    constructor(name, initialValue) {
        super();
        this.currentValue = initialValue;
        this.names.raw = name;
        this.names.capitalized = this.capitalizeFirstLetter(name);
        this.names.is = 'is' + this.names.capitalized;
        this.names.get = 'get' + this.names.capitalized;
        this.names.set = 'set' + this.names.capitalized;
    }

    register(wss, filter) {
        console.log('register event', this.names.is)
        wss.event(this.names.is);

        console.log('register setter', this.names.set)
        wss.register(this.names.set, newValue => {
            console.log(this.names.capitalized, this.names.set, newValue);
            if (filter) {
                newValue = filter(newValue);
            }
            //const oldValue = this.currentValue;
            this.value = newValue;
            console.log(this.names.capitalized, this.names.is, newValue);
            wss.emit(this.names.is, newValue);
            this.emit('value', newValue);
            return newValue;
        });

        console.log('register getter', this.names.get)
        wss.register(this.names.get, () => {
            console.log(this.names.capitalized, this.names.get, this.value);
            return this.value;
        });

        return this;
    }

    get value() {
        return this._currentValue;
    }

    set value(value) {
        this._currentValue = value;
    }

    get names() {
        return this._names;
    }

    capitalizeFirstLetter(string) {
        return string.charAt(0).toUpperCase() + string.slice(1);
    }
}

module.exports = {WsValue, booleanFilter}

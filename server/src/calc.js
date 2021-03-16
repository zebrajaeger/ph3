/*
Circle
d + (b*o) = (b*n) - ((n-1) * b * o)

n = d / (-b*o + b)
o = (b*n - d) / (b*n)


Partial
d = (b*n) - ((n-1) * b * o)

d = b*n - b*n*o + b*o
n = (-b * o + d) / (-b * o + b)
o = (-b*n + d) / (-b*n + b)
x = b*(1-0) * index;
 */
class Row {
    _isPartial = false;
    _targetSize;
    _sourceSize;
    _overlap;

    reset() {
        this._isPartial = false;
        this._targetSize = null;
        this._sourceSize = null;
        this._overlap = null;
    }

    calc() {
        const n = this.calcN();
        const o = this.calcO(n);

        return {n, o};
    }

    calcPos(o, index) {

    }

    calcN() {
        const b = this.sourceSize;
        const d = this.targetSize;
        const o = this.overlap;

        let n;
        if (this.isPartial) {
            n = (-b * o + d) / (-b * o + b)
        } else {
            n = d / (-b * o + b)
        }
        return Math.ceil(n);
    }

    calcO(n) {
        const b = this.sourceSize;
        const d = this.targetSize;

        let o;
        if (this.isPartial) {
            o = (-b * n + d) / (-b * n + b)
        } else {
            o = (b * n - d) / (b * n)
        }
        return o;
    }

    get isPartial() {
        return this._isPartial;
    }

    set isPartial(value) {
        this._isPartial = value;
    }

    get targetSize() {
        return this._targetSize;
    }

    set targetSize(value) {
        this._targetSize = value;
    }

    get sourceSize() {
        return this._sourceSize;
    }

    set sourceSize(value) {
        this._sourceSize = value;
    }

    get overlap() {
        return this._overlap;
    }

    set overlap(value) {
        this._overlap = value;
    }

    get count() {
        return this._count;
    }
}


module.exports = {Row}

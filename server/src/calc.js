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
    _targetStartPoint;
    _targetSize;
    _sourceSize;
    _overlap;

    reset() {
        this._isPartial = false;
        this._targetStartPoint = undefined;
        this._targetSize = undefined;
        this._sourceSize = undefined;
        this._overlap = undefined;
    }

    calc() {
        const n = this.calcN();
        const overlap = this.calcOverlap(n);
        const startPositions = this.calcStartPositions(n, overlap);
        return {n, overlap, startPositions};
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

    calcOverlap(n) {
        const b = this.sourceSize;
        const d = this.targetSize;

        let overlap;
        if (this.isPartial) {
            overlap = (-b * n + d) / (-b * n + b)
        } else {
            overlap = (b * n - d) / (b * n)
        }
        return overlap;
    }

    calcStartPositions(n, overlap) {
        const result = [];
        for (let i = 0; i < n; ++i) {
            result.push(this._targetStartPoint + (this.sourceSize * (1 - overlap) * i / n));
        }
        return result;
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

    get targetStartPoint() {
        return this._targetStartPoint;
    }

    set targetStartPoint(value) {
        this._targetStartPoint = value;
    }
}


module.exports = {Row}

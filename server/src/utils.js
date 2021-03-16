function map(x, inMin, inMax, outMin, outMax) {
    return (x - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
}

function isSet(o, cb) {
    const result = (o !== null && o !== undefined);
    if (result && cb) {
        cb(o);
    }
    return result;
}

function isUnset(o, cb) {
    const result = (o === null || o === undefined);
    if (result && cb) {
        cb(o);
    }
    return result;
}

module.exports = {map, isSet, isUnset};

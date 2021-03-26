const {HttpI2C} = require('@zebrajaeger/remote-i2c');
const WebSocketServer = require('rpc-websockets').Server

const Configstore = require('configstore');
const packageJson = require('../package.json');

const {I2CJoystick} = require('./joystick');
const {Controller} = require('./controller');

const {Row} = require('./calc')
// -----------

const config = new Configstore(packageJson.name);
const i2c = new HttpI2C('192.168.178.69', 8080);
const wsServer = new WebSocketServer({port: 8080, host: 'localhost'});

const maxVelocity = 1000;

// -----------

// CONTROLLER
wsServer.event('controller');
const controller = new Controller(i2c, 0x31, 1000);
controller.on('value', data => {

    wsServer.emit('controller', data)
});
controller.on('error', err => console.error(err));

wsServer.register('setTargetVelocity', async (axis, velocity) => {
    await controller.setTargetVelocity(axis, velocity);
})
wsServer.register('setTargetPos', async (axis, velocity) => {
    await controller.setTargetPos(axis, velocity);
})
wsServer.register('startFocus', async (timeMs) => {
    await controller.startFocus(timeMs);
})
wsServer.register('startTrigger', async (timeMs) => {
    await controller.startTrigger(timeMs);
})
wsServer.register('startShot', async (timeMs) => {
    await controller.startShot(timeMs);
})


// IS-JOGGING
let _jogging = false;

wsServer.event('jogging');

wsServer.register('getJogging', async (data) => {
    console.log('getJogging', _jogging);
    return _jogging;
});

wsServer.register('setJogging', async (data) => {
    console.log('setJogging', data)
    _jogging = data;
    wsServer.emit('jogging', _jogging)
    return _jogging;
});

// JOYSTICK
const _joystick = new I2CJoystick(i2c, 0x30, 250);
_joystick.threshold = 0.05;
_joystick.on('error', err => console.error(err));

wsServer.event('joystick');

_joystick.on('value', data => {
    wsServer.emit('joystick', data);
    if (_jogging.isJogging === true) {
        // TODO maybe x²..x³
        const x = data.x.calculated.capped;
        controller.setTargetVelocity(0, (x - 0.5) * 2 * maxVelocity).then();
        // TODO maybe y²..y³
        const y = data.y.calculated.capped;
        controller.setTargetVelocity(1, (y - 0.5) * 2 * maxVelocity).then();
    }
});


// JOYSTICK.calibraion
const CONFIG_JOYSTICK_CALIBRATION = 'joystick.calibration'
if (config.get(CONFIG_JOYSTICK_CALIBRATION)) {
    _joystick.autoCalibrationEnabled = false;
    _joystick.calibration = config.get(CONFIG_JOYSTICK_CALIBRATION);
    console.log(CONFIG_JOYSTICK_CALIBRATION, _joystick.calibration)
} else {
    _joystick.autoCalibrationEnabled = true;
}
wsServer.register('setJoystickCalibrationReset', () => {
    console.log('setJoystickCalibrationReset');
    _joystick.resetCalibration();
    _joystick.autoCalibrationEnabled = true;
    return null;
})
wsServer.register('setSaveJoystickCalibrationData', () => {
    console.log('setSaveJoystickCalibrationData');
    _joystick.autoCalibrationEnabled = false;
    _joystick.setCurrentPositionAsCenter();
    console.log(CONFIG_JOYSTICK_CALIBRATION, _joystick.calibration)
    config.set(CONFIG_JOYSTICK_CALIBRATION, _joystick.calibration);
    return null;
})


// ============================== PANO ==============================

// PANO

let _pano = {
    overlap: 0.25,
    x: {n: undefined, overlap: undefined, startPositions: undefined},
    y: {n: undefined, overlap: undefined, startPositions: undefined}
}

wsServer.event('pano');

function recalcPanoAndNotifyClients() {
    const tempPano = calcPano();
    if (tempPano) {
        _pano.x = tempPano.x;
        _pano.y = tempPano.y;
    } else {
        _pano.x = {n: null, overlap: null}
        _pano.y = {n: null, overlap: null}
    }
    console.log('recalcPanoAndNotifyClients', _pano);
    wsServer.emit('pano', _pano)
}

wsServer.register('getPano', async (data) => {
    console.log('getPano', _pano);
    return _pano;
})


// CAMERA-FOV
const CONFIG_CAMERA_FOV = 'camera.fov'
let _cameraFOV = {x1: null, y1: null, x2: null, y2: null}
{
    // init
    if (config.has(CONFIG_CAMERA_FOV)) {
        _cameraFOV = config.get(CONFIG_CAMERA_FOV)
        console.log(CONFIG_CAMERA_FOV, 'from config', _cameraFOV)
    } else {
        console.log(CONFIG_CAMERA_FOV, 'default', _cameraFOV)
    }
    wsServer.event('cameraFov');
    wsServer.emit('cameraFov', _cameraFOV);
}

wsServer.register('getCameraFov', async (data) => {
    console.log('getCameraFov', _cameraFOV);
    return _cameraFOV;
})

wsServer.register('setCameraFov', async (data) => {
    console.log('setCameraFov', data);
    _cameraFOV = data;
    config.set(CONFIG_CAMERA_FOV, _cameraFOV);
    wsServer.emit('cameraFov', _cameraFOV);

    recalcPanoAndNotifyClients();
    return _cameraFOV;
})


// PANO-FOV
let _panoFOV = {x1: null, y1: null, x2: null, y2: null}
const CONFIG_PANO_FOV = 'pano.fov'
{
    // init
    if (config.has(CONFIG_PANO_FOV)) {
        _panoFOV = config.get(CONFIG_PANO_FOV)
        console.log(CONFIG_PANO_FOV, 'from config', _panoFOV)
    } else {
        console.log(CONFIG_PANO_FOV, 'default', _panoFOV)
    }
    wsServer.event('panoFov');
    wsServer.emit('panoFov', _panoFOV);
}

wsServer.register('getPanoFov', async (data) => {
    console.log('getPanoFov', _panoFOV);
    return _panoFOV;
})

wsServer.register('setPanoFov', async (data) => {
    console.log('setPanoFov', data);
    _panoFOV = data;
    config.set(CONFIG_PANO_FOV, _panoFOV);
    wsServer.emit('panoFov', _panoFOV);
    recalcPanoAndNotifyClients();
    return _panoFOV;
})


// PANO-SETTINGS
let _panoSettings = {minOverlapX: 25, minOverlapY: 25}
const CONFIG_PANO_SETTINGS = 'pano.settings'
{
    // init
    if (config.has(CONFIG_PANO_SETTINGS)) {
        _panoSettings = config.get(CONFIG_PANO_SETTINGS)
        console.log(CONFIG_PANO_SETTINGS, 'from config', _panoSettings)
    } else {
        console.log(CONFIG_PANO_SETTINGS, 'default', _panoSettings)
    }
    wsServer.event('panoSettings');
    wsServer.emit('panoSettings', _panoSettings);
    recalcPanoAndNotifyClients();
}

wsServer.register('getPanoSettings', async (data) => {
    console.log('getPanoSettings', _panoSettings);
    return _panoSettings;
})

wsServer.register('setPanoSettings', async (data) => {
    // TODO check type/values
    console.log('setPanoSettings to ', data);
    _panoSettings = data;
    config.set(CONFIG_PANO_SETTINGS, _panoSettings)
    wsServer.emit('panoSettings', _panoSettings)
    recalcPanoAndNotifyClients();
})

function calcPano() {
    const camera = normalizeCameraView();
    const pano = normalizePanoView();
    if ((camera !== undefined) && (pano !== undefined)) {
        const row = new Row();
        const tempPano = {
            overlap: _pano.overlap,
            x: {n: undefined, overlap: undefined, startPositions: undefined},
            y: {n: undefined, overlap: undefined, startPositions: undefined}
        };

        {
            // W
            const row = new Row();
            row.targetStartPoint = pano.x1;
            row.sourceSize = camera.w;
            row.targetSize = pano.w;
            row.overlap = tempPano.overlap;
            let v1 = row.calc();
            tempPano.x.n = v1.n;
            tempPano.x.overlap = v1.overlap;
            tempPano.x.startPositions = v1.startPositions;
        }

        {
            // H
            const row = new Row();

            row.targetStartPoint = pano.y1;
            row.sourceSize = camera.h;
            row.targetSize = pano.h;
            row.overlap = tempPano.overlap;
            let v2 = row.calc();
            tempPano.y.n = v2.n;
            tempPano.y.overlap = v2.overlap;
            tempPano.y.startPositions = v2.startPositions;
        }

        console.log('RECALC PANO', {camera, pano, tempPano})
        return tempPano;
    } else {
        return null;
    }
}

function normalizeCameraView() {
    if (_cameraFOV.x1 !== undefined && _cameraFOV.y1 !== undefined && _cameraFOV.y2 !== undefined && _cameraFOV.x2 !== undefined) {
        return {w: Math.abs(_cameraFOV.x1 - _cameraFOV.x2), h: Math.abs(_cameraFOV.y1 - _cameraFOV.y2)}
    } else {
        return undefined;
    }
}

function normalizePanoView() {
    if (_panoFOV.x1 !== undefined && _panoFOV.y1 !== undefined && _panoFOV.y2 !== undefined && _panoFOV.x2 !== undefined) {
        let x1, x2, y1, y2;
        if (_panoFOV.x1 < _panoFOV.x2) {
            x1 = _panoFOV.x1;
            x2 = _panoFOV.x2;
        } else {
            x1 = _panoFOV.x2;
            x2 = _panoFOV.x1;
        }
        if (_panoFOV.y1 < _panoFOV.y2) {
            y1 = _panoFOV.y1;
            y2 = _panoFOV.y2;
        } else {
            y1 = _panoFOV.y2;
            y2 = _panoFOV.y1;
        }
        return {x1, y1, x2, y2, w: x2 - x1, h: y2 - y1}
    } else {
        return undefined;
    }
}

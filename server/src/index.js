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
let jogging = false;

wsServer.event('jogging');

wsServer.register('getJogging', async (data) => {
    console.log('getJogging', jogging);
    return jogging;
});

wsServer.register('setJogging', async (data) => {
    console.log('setJogging', data)
    jogging = data;
    wsServer.emit('jogging', jogging)
    return jogging;
});

// JOYSTICK
const joystick = new I2CJoystick(i2c, 0x30, 250);
joystick.threshold = 0.05;
joystick.on('error', err => console.error(err));

wsServer.event('joystick');

joystick.on('value', data => {
    wsServer.emit('joystick', data);
    if (jogging.isJogging === true) {
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
    joystick.autoCalibrationEnabled = false;
    joystick.calibration = config.get(CONFIG_JOYSTICK_CALIBRATION);
    console.log(CONFIG_JOYSTICK_CALIBRATION, joystick.calibration)
} else {
    joystick.autoCalibrationEnabled = true;
}
wsServer.register('setJoystickCalibrationReset', () => {
    console.log('setJoystickCalibrationReset');
    joystick.resetCalibration();
    joystick.autoCalibrationEnabled = true;
    return null;
})
wsServer.register('setSaveJoystickCalibrationData', () => {
    console.log('setSaveJoystickCalibrationData');
    joystick.autoCalibrationEnabled = false;
    joystick.setCurrentPositionAsCenter();
    console.log(CONFIG_JOYSTICK_CALIBRATION, joystick.calibration)
    config.set(CONFIG_JOYSTICK_CALIBRATION, joystick.calibration);
    return null;
})


// ============================== PANO ==============================

// PANO

let pano = {overlap: 0.25, x: {n: null, overlap: null}, y: {n: null, overlap: null}}

wsServer.event('pano');

function recalcPanoAndNotifyClients() {
    const tempPano = calcPano();
    if (tempPano) {
        pano.x = tempPano.x;
        pano.y = tempPano.y;
    } else {
        pano.x = {n: null, overlap: null}
        pano.y = {n: null, overlap: null}
    }
    console.log('recalcPanoAndNotifyClients', pano);
    wsServer.emit('pano', pano)
}

wsServer.register('getPano', async (data) => {
    console.log('getPano', pano);
    return pano;
})


// CAMERA-FOV
const CONFIG_CAMERA_FOV = 'camera.fov'
let cameraFOV = {x1: null, y1: null, x2: null, y2: null}
{
    // init
    if (config.has(CONFIG_CAMERA_FOV)) {
        cameraFOV = config.get(CONFIG_CAMERA_FOV)
        console.log(CONFIG_CAMERA_FOV, 'from config', cameraFOV)
    } else {
        console.log(CONFIG_CAMERA_FOV, 'default', cameraFOV)
    }
    wsServer.event('cameraFov');
    wsServer.emit('cameraFov', cameraFOV);
}

wsServer.register('getCameraFov', async (data) => {
    console.log('getCameraFov', cameraFOV);
    return cameraFOV;
})

wsServer.register('setCameraFov', async (data) => {
    console.log('setCameraFov', data);
    cameraFOV = data;
    config.set(CONFIG_CAMERA_FOV, cameraFOV);
    wsServer.emit('cameraFov', cameraFOV);

    recalcPanoAndNotifyClients();
    return cameraFOV;
})


// PANO-FOV
let panoFOV = {x1: null, y1: null, x2: null, y2: null}
const CONFIG_PANO_FOV = 'pano.fov'
{
    // init
    if (config.has(CONFIG_PANO_FOV)) {
        panoFOV = config.get(CONFIG_PANO_FOV)
        console.log(CONFIG_PANO_FOV, 'from config', panoFOV)
    } else {
        console.log(CONFIG_PANO_FOV, 'default', panoFOV)
    }
    wsServer.event('panoFov');
    wsServer.emit('panoFov', panoFOV);
}

wsServer.register('getPanoFov', async (data) => {
    console.log('getPanoFov', panoFOV);
    return panoFOV;
})

wsServer.register('setPanoFov', async (data) => {
    console.log('setPanoFov', data);
    panoFOV = data;
    config.set(CONFIG_PANO_FOV, panoFOV);
    wsServer.emit('panoFov', panoFOV);
    recalcPanoAndNotifyClients();
    return panoFOV;
})


// PANO-SETTINGS
let panoSettings = {minOverlapX: 25, minOverlapY: 25}
const CONFIG_PANO_SETTINGS = 'pano.settings'
{
    // init
    if (config.has(CONFIG_PANO_SETTINGS)) {
        panoSettings = config.get(CONFIG_PANO_SETTINGS)
        console.log(CONFIG_PANO_SETTINGS, 'from config', panoSettings)
    } else {
        console.log(CONFIG_PANO_SETTINGS, 'default', panoSettings)
    }
    wsServer.event('panoSettings');
    wsServer.emit('panoSettings', panoSettings);
    recalcPanoAndNotifyClients();
}

wsServer.register('getPanoSettings', async (data) => {
    console.log('getPanoSettings', panoSettings);
    return panoSettings;
})

wsServer.register('setPanoSettings', async (data) => {
    // TODO check type/values
    console.log('setPanoSettings to ', data);
    panoSettings = data;
    config.set(CONFIG_PANO_SETTINGS, panoSettings)
    wsServer.emit('panoSettings', data)
    recalcPanoAndNotifyClients();
})


function calcPano() {
    const c = calcCameraView();
    const p = calcPanoView();
    if ((c !== null) && (p !== null)) {
        const row = new Row();
        const tempPano = {overlap: pano.overlap, x: {n: null, overlap: null}, y: {n: null, overlap: null}};
        // W
        row.sourceSize = c.w;
        row.targetSize = p.w;
        row.overlap = tempPano.overlap;
        let v1 = row.calc();
        tempPano.x.n = v1.n;
        tempPano.x.overlap = v1.overlap;
        row.reset();

        // H
        row.sourceSize = c.h;
        row.targetSize = p.h;
        row.overlap = tempPano.overlap;
        let v2 = row.calc();
        tempPano.y.n = v2.n;
        tempPano.y.overlap = v2.overlap;

        console.log('RECALC PANO', {v1, v2, c, p, tempPano})
        return tempPano;
    } else {
        return null;
    }
}

function calcCameraView() {
    if (cameraFOV.x1 != null && cameraFOV.y1 != null && cameraFOV.y2 != null && cameraFOV.x2 != null) {
        return {w: Math.abs(cameraFOV.x1 - cameraFOV.x2), h: Math.abs(cameraFOV.y1 - cameraFOV.y2)}
    } else {
        return null;
    }
}

function calcPanoView() {
    if (panoFOV.x1 != null && panoFOV.y1 != null && panoFOV.y2 != null && panoFOV.x2 != null) {
        let x1, x2, y1, y2;
        if (panoFOV.x1 < panoFOV.x2) {
            x1 = panoFOV.x1;
            x2 = panoFOV.x2;
        } else {
            x1 = panoFOV.x2;
            x2 = panoFOV.x1;
        }
        if (panoFOV.y1 < panoFOV.y2) {
            y1 = panoFOV.y1;
            y2 = panoFOV.y2;
        } else {
            y1 = panoFOV.y2;
            y2 = panoFOV.y1;
        }
        return {x1, y1, x2, y2, w: x2 - x1, h: y2 - y1}
    } else {
        return null;
    }
}

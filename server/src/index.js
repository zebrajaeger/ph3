const {HttpI2C} = require('@zebrajaeger/remote-i2c');
const WebSocketServer = require('rpc-websockets').Server

const Configstore = require('configstore');
const packageJson = require('../package.json');

const {I2CJoystick} = require('./joystick');
const {Controller} = require('./controller');

const {Row} = require('./calc')
// -----------

const config = new Configstore(packageJson.name, {foo: 'bar'});
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
if (config.get('joystick.calibration')) {
    joystick.autoCalibrationEnabled = false;
    joystick.calibration = config.get('joystick.calibration');
    console.log('JOYSTICK.calibraion', joystick.calibration)
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
    console.log('JOYSTICK.calibration', joystick.calibration)
    config.set('joystick.calibration', joystick.calibration);
    return null;
})


// ============================== PANO ==============================

// PANO

let pano = {o: 0.25, x: {n: null, o: null}, y: {n: null, o: null}}

wsServer.event('pano');

function recalcPanoAndNotifyClients() {
    const tempPano = calcPano();
    if (tempPano) {
        pano.x = tempPano.x;
        pano.y = tempPano.y;
    } else {
        pano.x = {n: null, o: null}
        pano.y = {n: null, o: null}
    }
    wsServer.emit('pano', pano)
}

// CAMERA-FOV
let cameraFOV = {x1: null, y1: null, x2: null, y2: null}
{
    // init
    if (config.has('camera.fov')) {
        cameraFOV = config.get('camera.fov')
        console.log('camera.fov', 'from config', cameraFOV)
    } else {
        console.log('camera.fov', 'default', cameraFOV)
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
    if (data.x1 !== undefined) {
        cameraFOV.x1 = data.x1;
    }
    if (data.x2 !== undefined) {
        cameraFOV.x2 = data.x2;
    }
    if (data.y1 !== undefined) {
        cameraFOV.y1 = data.y1;
    }
    if (data.y2 !== undefined) {
        cameraFOV.y2 = data.y2;
    }
    config.set('camera.fov', cameraFOV);
    wsServer.emit('cameraFov', cameraFOV);

    recalcPanoAndNotifyClients();
    return cameraFOV;
})


// PANO-FOV
let panoFOV = {x1: null, y1: null, x2: null, y2: null}

wsServer.event('panoFov');

wsServer.register('getPanoFov', async (data) => {
    console.log('getPanoFov', panoFOV);
    return panoFOV;
})

wsServer.register('setPanoFov', async (data) => {
    console.log('setPanoFov', data);
    if (data.x1 !== undefined) {
        panoFOV.x1 = data.x1;
    }
    if (data.x2 !== undefined) {
        panoFOV.x2 = data.x2;
    }
    if (data.y1 !== undefined) {
        panoFOV.y1 = data.y1;
    }
    if (data.y2 !== undefined) {
        panoFOV.y2 = data.y2;
    }
    wsServer.emit('panoFov', panoFOV);
    recalcPanoAndNotifyClients();
    return panoFOV;
})


// PANO-SETTINGS
let panoSettings = {minOverlapX: 25, minOverlapY: 25}

wsServer.event('panoSettings');

wsServer.register('getPanoSettings', async (data) => {
    console.log('getPanoSettings', panoSettings);
    return panoSettings;
})

wsServer.register('setPanoSettings', async (data) => {
    // TODO check type/values
    console.log('setPanoSettings to ', data);
    panoSettings = data;
    wsServer.emit('panoSettings', data)
    recalcPanoAndNotifyClients();
})


function calcPano() {
    const c = calcCameraView();
    const p = calcPanoView();
    if ((c !== null) && (p !== null)) {
        const row = new Row();
        const tempPano = {o: pano.o, x: {n: null, o: null}, y: {n: null, o: null}};
        // W
        row.sourceSize = c.w;
        row.targetSize = p.w;
        row.overlap = tempPano.o;
        let v = row.calc();
        tempPano.x.n = v.n;
        tempPano.x.o = v.o;
        row.reset();

        // H
        row.sourceSize = c.h;
        row.targetSize = p.h;
        row.overlap = tempPano.o;
        v = row.calc();
        tempPano.y.n = v.n;
        tempPano.y.o = v.o;

        return pano;
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
        if (panoFOV.x1 > panoFOV.x2) {
            x1 = panoFOV.x1;
            x2 = panoFOV.x2;
        } else {
            x1 = panoFOV.x2;
            x2 = panoFOV.x1;
        }
        if (panoFOV.y1 > panoFOV.y2) {
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

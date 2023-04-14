#include <Arduino.h>
#include <Wire.h>
#include <avr/boot.h>
#include <avr/pgmspace.h>

#include "./stepperdriver.h"
#include "camera.h"
#include "default_config.h"
#include "timer.h"
#include "types.h"
#include "wireutils.h"

enum command_t {
  stepperWriteLimit = 0x20,
  stepperWriteVelocity = 0x21,
  stepperWriteTargetPos = 0x22,
  stepperStopAll = 0x23,

  stepperWriteActualAndTargetPos = 0x27,
  stepperWriteActualPos = 0x28,
  stepperResetPos = 0x29,

  cameraStartFocus = 0x30,
  cameraStartTrigger = 0x31,
  cameraStartShot = 0x32,

  unknown = 127
};

bool debug = true;
StepperDriver stepperDriver;
Camera camera;
command_t command_ = unknown;

uint32_t loopCounter = 0;
unsigned long lastLoopTime = 0;

class StatisticTimer : public IntervalTimer {
  virtual void onTimer() {
    unsigned long now = millis();
    uint32_t diff = now - lastLoopTime;
    Serial.print("LPS: ");
    Serial.println(loopCounter * 1000 / diff);
    loopCounter = 0;
    lastLoopTime = now;
    stepperDriver.statistic();
  }
};
StatisticTimer statisticTimer;

// -----------------------------------------------------------------------------
void alarm()
// -----------------------------------------------------------------------------
{
  for (;;) {
    digitalWrite(LED_BUILTIN, 1);
    digitalWrite(LED_PIN_A, 1);
    digitalWrite(LED_MOVE_PIN_X, 1);
    digitalWrite(LED_MOVE_PIN_Y, 1);

    delay(25);

    digitalWrite(LED_BUILTIN, 0);
    digitalWrite(LED_PIN_A, 0);
    digitalWrite(LED_MOVE_PIN_X, 0);
    digitalWrite(LED_MOVE_PIN_Y, 0);

    delay(150);
  }
}

// -----------------------------------------------------------------------------
void requestEvent()
// -----------------------------------------------------------------------------
{
  const StepperDriver::Stepper_t &s1 = stepperDriver.getStepper(0);
  const StepperDriver::Stepper_t &s2 = stepperDriver.getStepper(1);
  WireUtils::write8(stepperDriver.getMovementStatus().u8);
  WireUtils::write32(s1.pos);
  WireUtils::write16(s1.speed);
  WireUtils::write32(s2.pos);
  WireUtils::write16(s2.speed);
  WireUtils::write8(camera.getStatus().u8);
  // 1 + 4 + 2 + 4 + 2 + 1 = 14
}

// -----------------------------------------------------------------------------
void onWriteTargetPos()
// -----------------------------------------------------------------------------
{
  u8_t axis;
  u32_t pos;
  if (WireUtils::read8(axis) && WireUtils::read32(pos)) {
    if (debug) {
      Serial.print("Write Target Pos: ");
      Serial.print(axis.uint8);
      Serial.print(": ");
      Serial.println(pos.uint32);
    }
    stepperDriver.setPos(axis, pos);
  } else {
    Serial.println(F("; NOT ENOUGH DATA"));
  }
}

// -----------------------------------------------------------------------------
void onWriteVelocity()
// -----------------------------------------------------------------------------
{
  u8_t axis;
  u32_t velocity;
  if (WireUtils::read8(axis) && WireUtils::read32(velocity)) {
    if (debug) {
      Serial.print("V: ");
      Serial.print(axis.uint8);
      Serial.print(",");
      Serial.println(velocity.int32);
    }
    stepperDriver.setVelocity(axis, velocity);
  } else {
    Serial.println(F("; NOT ENOUGH DATA"));
  }
}

// -----------------------------------------------------------------------------
void onWriteActualPos()
// -----------------------------------------------------------------------------
{
  u8_t axis;
  u32_t pos;
  if (WireUtils::read8(axis) && WireUtils::read32(pos)) {
    if (debug) {
      Serial.print("Write Actual Pos: ");
      Serial.print(axis.uint8);
      Serial.print(": ");
      Serial.println(pos.uint32);
    }
    stepperDriver.setActualPos(axis, pos);
  } else {
    Serial.println(F("; NOT ENOUGH DATA"));
  }
}

// -----------------------------------------------------------------------------
void onWriteActualAndTargetPos()
// -----------------------------------------------------------------------------
{
  u8_t axis;
  u32_t pos;
  if (WireUtils::read8(axis) && WireUtils::read32(pos)) {
    if (debug) {
      Serial.print("Write Actual and Target Pos: ");
      Serial.print(axis.uint8);
      Serial.print(": ");
      Serial.println(pos.uint32);
    }
    stepperDriver.setPos(axis, pos);
    stepperDriver.setActualPos(axis, pos);
  } else {
    Serial.println(F("; NOT ENOUGH DATA"));
  }
}

// -----------------------------------------------------------------------------
void onResetPos()
// -----------------------------------------------------------------------------
{
  stepperDriver.resetAllPos();
}

// -----------------------------------------------------------------------------
void onStopAll()
// -----------------------------------------------------------------------------
{
  stepperDriver.stopAll();
}

// -----------------------------------------------------------------------------
void onWriteLimit()
// -----------------------------------------------------------------------------
{
  u8_t axis;
  u32_t velocityMinHz;
  u32_t velocityMaxHz;
  u32_t accelerationMaxHzPerSecond;

  if (WireUtils::read8(axis) && WireUtils::read32(velocityMinHz) &&
      WireUtils::read32(velocityMaxHz) &&
      WireUtils::read32(accelerationMaxHzPerSecond)) {
    StepperDriver::Limit_t limit;
    limit.velocityMinHz = velocityMinHz.uint32;
    limit.velocityMaxHz = velocityMaxHz.uint32;
    limit.acceleration_max_hz_per_s = accelerationMaxHzPerSecond.uint32;
    stepperDriver.setLimit(axis, limit);
  } else {
    Serial.println(F("; NOT ENOUGH DATA"));
  }
}

// -----------------------------------------------------------------------------
void onTriggerCameraFocus()
// -----------------------------------------------------------------------------
{
  u32_t ms;
  if (WireUtils::read32(ms)) {
    camera.startFocus(ms);
  }
}

// -----------------------------------------------------------------------------
void onTriggerCameraTrigger()
// -----------------------------------------------------------------------------
{
  u32_t ms;
  if (WireUtils::read32(ms)) {
    camera.startTrigger(ms);
  }
}

// -----------------------------------------------------------------------------
void onTriggerCameraShot()
// -----------------------------------------------------------------------------
{
  u32_t focusMs;
  u32_t triggerMs;
  if (WireUtils::read32(focusMs) && WireUtils::read32(triggerMs)) {
    camera.startShot(focusMs, triggerMs);
  }
}

// -----------------------------------------------------------------------------
void receiveEvent(int howMany)
// -----------------------------------------------------------------------------
{
  // Serial.print(F("receiveEvent n:"));
  // Serial.println(howMany);
  /*
  for(uint8_t i=0; i<howMany; ++i){
      Serial.print("  ");
      Serial.println(Wire.read());
  }
  */

  u8_t temp;
  if (WireUtils::read8(temp)) {
    command_ = (command_t)temp.uint8;
    switch (command_) {
      case stepperWriteLimit:
        onWriteLimit();
        break;
      case stepperWriteTargetPos:
        onWriteTargetPos();
        break;
      case stepperWriteVelocity:
        onWriteVelocity();
        break;
      case stepperStopAll:
        onStopAll();
        break;

      case stepperResetPos:
        onResetPos();
        break;
      case stepperWriteActualPos:
        onWriteActualPos();
        break;
      case stepperWriteActualAndTargetPos:
        onWriteActualAndTargetPos();
        break;

      case cameraStartFocus:
        onTriggerCameraFocus();
        break;
      case cameraStartTrigger:
        onTriggerCameraTrigger();
        break;
      case cameraStartShot:
        onTriggerCameraShot();
        break;
      default: {
        Serial.print(F("receiveEvent: UNKOWN COMMAND: "));
        Serial.println(command_);
      }
    }
  }

  // remove all pending data because it would suppress next call of this
  // function
  while (Wire.available() > 0) {
    Wire.read();
  }
}

// -----------------------------------------------------------------------------
void setup()
// -----------------------------------------------------------------------------
{
  Serial.begin(115200);

  // Fuses
  uint8_t lowBits = boot_lock_fuse_bits_get(GET_LOW_FUSE_BITS);
  uint8_t highBits = boot_lock_fuse_bits_get(GET_HIGH_FUSE_BITS);
  uint8_t extendedBits = boot_lock_fuse_bits_get(GET_EXTENDED_FUSE_BITS);
  uint8_t lockBits = boot_lock_fuse_bits_get(GET_LOCK_BITS);
  Serial.print(F("Low:  0x"));
  Serial.println(lowBits, HEX);
  Serial.print(F("High: 0x"));
  Serial.println(highBits, HEX);
  Serial.print(F("Ext:  0x"));
  Serial.println(extendedBits, HEX);
  Serial.print(F("Lock: 0x"));
  Serial.println(lockBits, HEX);

  // check that clockout is set
  if ((lowBits & 0x40) != 0) {
    Serial.println(
        F("ERROR: CKOUT-bit on low-efuse is set. That mean the TMC429 has no "
          "16MHz clock and cannot work."));
    pinMode(LED_BUILTIN, OUTPUT);
    alarm();
  }

  // LEDs
  pinMode(LED_MOVE_PIN_X, OUTPUT);
  digitalWrite(LED_MOVE_PIN_X, false);
  pinMode(LED_MOVE_PIN_Y, OUTPUT);
  digitalWrite(LED_MOVE_PIN_Y, false);
  pinMode(LED_PIN_A, OUTPUT);
  digitalWrite(LED_PIN_A, false);

  // I²C
  Wire.begin(I2C_ADDRESS);
  Wire.onRequest(requestEvent);
  Wire.onReceive(receiveEvent);

  // TMC429
  StepperDriver::Limit_t limit = {16 * 10, 200 * 16 * 7, 75000};
  StepperDriver::Limit_t limits[3] = {limit, limit, limit};
  if (stepperDriver.setup(CS_PIN, TMC_CLOCK_MHZ, limits)) {
    Serial.println(F("StepperDriver initialized"));
  } else {
    Serial.println(F("ERROR: StepperDriver NOT initialized"));
  }

  // Camera
  if (camera.setup(CAMERA_FOCUS_PIN, CAMERA_TRIGGER_PIN)) {
    Serial.println(F("Camera initialized"));
  } else {
    Serial.println(F("ERROR: Camera NOT initialized"));
  }

  // statistic
  statisticTimer.start(1000, false);

  Serial.println(F("################### START ###################"));
}

// -----------------------------------------------------------------------------
void loop()
// -----------------------------------------------------------------------------
{
  loopCounter++;
  stepperDriver.loop();
  digitalWrite(LED_MOVE_PIN_X, stepperDriver.getStepper(0).speed.uint16 != 0);
  digitalWrite(LED_MOVE_PIN_Y, stepperDriver.getStepper(1).speed.uint16 != 0);
  digitalWrite(LED_PIN_A, stepperDriver.isMoving());

  camera.loop();
  statisticTimer.loop();
}

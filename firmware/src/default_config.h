#pragma once

#if __has_include("config.h")
#include "config.h"
#endif

// Steppers
#ifndef CS_PIN
#define CS_PIN 10
#endif

#ifndef TMC_CLOCK_MHZ
#define TMC_CLOCK_MHZ 16
#endif

#ifndef I2C_ADDRESS
#define I2C_ADDRESS 0x33
#endif

#ifndef LED_PIN_A
#define LED_PIN_A 5
#endif

#ifndef LED_MOVE_PIN_X
#define LED_MOVE_PIN_X 3
#endif

#ifndef LED_MOVE_PIN_Y
#define LED_MOVE_PIN_Y 4
#endif

// Camera
#ifndef CAMERA_FOCUS_PIN
#define CAMERA_FOCUS_PIN 7
#endif

#ifndef CAMERA_TRIGGER_PIN
#define CAMERA_TRIGGER_PIN 6
#endif

